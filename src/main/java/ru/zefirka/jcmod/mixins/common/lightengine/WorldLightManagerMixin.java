package ru.zefirka.jcmod.mixins.common.lightengine;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.ILightListener;
import net.minecraft.world.lighting.IWorldLightListener;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.lighting.WorldLightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.lightoptimizer.common.chunk.ExtendedChunk;
import ru.zefirka.jcmod.lightoptimizer.common.light.SWMRNibbleArray;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightEngine;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightInterface;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightLightingProvider;
import ru.zefirka.jcmod.lightoptimizer.common.util.CoordinateUtils;
import ru.zefirka.jcmod.lightoptimizer.common.util.WorldUtil;

import javax.annotation.Nullable;

@Mixin(WorldLightManager.class)
public abstract class WorldLightManagerMixin implements ILightListener, StarLightLightingProvider {

    @Shadow
    @Nullable
    private LightEngine<?, ?> blockEngine;

    @Shadow
    @Nullable
    private LightEngine<?, ?> skyEngine;

    @Unique
    protected StarLightInterface lightEngine;

    @Override
    public final StarLightInterface getLightEngine() {
        return this.lightEngine;
    }

    /**
     *
     * TODO since this is a constructor inject, check on update for new constructors
     */
    @Inject(
            method = "<init>", at = @At("TAIL")
    )
    public void construct(final IChunkLightProvider provider, final boolean hasBlockLight, final boolean hasSkyLight,
                          final CallbackInfo ci) {
        if (provider.getLevel() instanceof World) {
            this.lightEngine = new StarLightInterface(provider, hasSkyLight, hasBlockLight);
        } else {
            this.lightEngine = new StarLightInterface(null, hasSkyLight, hasBlockLight);
        }
        // intentionally destroy mods hooking into old light engine state
        this.blockEngine = null;
        this.skyEngine = null;
    }

    /**
     * @reason Route to new light engine
     * @author Spottedleaf
     */
    @Overwrite
    public void checkBlock(final BlockPos pos) {
        this.lightEngine.blockChange(pos.immutable());
    }

    /**
     * @reason Avoid messing with vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void onBlockEmissionIncrease(final BlockPos pos, final int level) {
        // this light engine only reads levels from blocks, so this is a no-op
    }

    /**
     * @reason Route to new light engine
     * @author Spottedleaf
     */
    @Overwrite
    public boolean hasLightWork() {
        // route to new light engine
        return this.lightEngine.hasUpdates();
    }

    /**
     * @reason Hook into new light engine for light updates
     * @author Spottedleaf
     */
    @Overwrite
    public int runUpdates(final int maxUpdateCount, final boolean doSkylight, final boolean skipEdgeLightPropagation) {
        // replace impl
        final boolean hadUpdates = this.hasLightWork();
        this.lightEngine.propagateChanges();
        return hadUpdates ? 1 : 0;
    }

    /**
     * @reason New light engine hook for handling empty section changes
     * @author Spottedleaf
     */
    @Overwrite
    public void updateSectionStatus(final SectionPos pos, final boolean notReady) {
        this.lightEngine.sectionChange(pos, notReady);
    }

    /**
     * @reason Avoid messing with the vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void enableLightSources(final ChunkPos pos, final boolean lightEnabled) {

    }

    /**
     * @reason Replace light views with our own that hook into the new light engine instead of vanilla's
     * @author Spottedleaf
     */
    @Overwrite
    public IWorldLightListener getLayerListener(final LightType lightType) {
        return lightType == LightType.BLOCK ? this.lightEngine.getBlockReader() : this.lightEngine.getSkyReader();
    }

    /**
     * @reason Avoid messing with the vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void queueSectionData(final LightType lightType, final SectionPos pos, final NibbleArray nibble,
                        final boolean trustEdges) {
    }

    /**
     * @reason Avoid messing with the vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void retainData(final ChunkPos pos, final boolean retainData) {
        // not used by new light impl
    }

    /**
     * @reason Need to use our own hooks for retrieving light data
     * @author Spottedleaf
     */
    @Overwrite
    public int getRawBrightness(final BlockPos pos, final int ambientDarkness) {
        // need to use new light hooks for this
        final int sky = this.lightEngine.getSkyReader().getLightValue(pos) - ambientDarkness;
        final int block = this.lightEngine.getBlockReader().getLightValue(pos);
        return Math.max(sky, block);
    }

    @Unique
    protected final Long2ObjectOpenHashMap<SWMRNibbleArray[]> blockLightMap = new Long2ObjectOpenHashMap<>();

    @Unique
    protected final Long2ObjectOpenHashMap<SWMRNibbleArray[]> skyLightMap = new Long2ObjectOpenHashMap<>();

    @Override
    public void clientUpdateLight(final LightType lightType, SectionPos pos, final @Nullable NibbleArray nibble,
                                  final boolean trustEdges) {
        if (((Object)this).getClass() != WorldLightManager.class) {
            throw new IllegalStateException("This hook is for the CLIENT ONLY");
        }
        // data storage changed with new light impl
        final IChunk chunk = this.getLightEngine().getAnyChunkNow(pos.getX(), pos.getZ());
        switch (lightType) {
            case BLOCK: {
                final SWMRNibbleArray[] blockNibbles = this.blockLightMap.computeIfAbsent(CoordinateUtils.getChunkKey(pos), (final long keyInMap) -> {
                    return StarLightEngine.getFilledEmptyLight(this.lightEngine.getWorld());
                });

                blockNibbles[pos.getY() - WorldUtil.getMinLightSection(this.lightEngine.getWorld())] = SWMRNibbleArray.fromVanilla(nibble);

                if (chunk != null) {
                    ((ExtendedChunk)chunk).setBlockNibbles(blockNibbles);
                    this.lightEngine.getLightAccess().onLightUpdate(LightType.BLOCK, pos);
                }
                break;
            }
            case SKY: {
                final SWMRNibbleArray[] skyNibbles = this.skyLightMap.computeIfAbsent(CoordinateUtils.getChunkKey(pos), (final long keyInMap) -> {
                    return StarLightEngine.getFilledEmptyLight(this.lightEngine.getWorld());
                });

                skyNibbles[pos.getY() - WorldUtil.getMinLightSection(this.lightEngine.getWorld())] = SWMRNibbleArray.fromVanilla(nibble);

                if (chunk != null) {
                    ((ExtendedChunk)chunk).setSkyNibbles(skyNibbles);
                    this.lightEngine.getLightAccess().onLightUpdate(LightType.SKY, pos);
                }
                break;
            }
        }
    }

    @Override
    public void clientRemoveLightData(final ChunkPos chunkPos) {
        if (((Object)this).getClass() != WorldLightManager.class) {
            throw new IllegalStateException("This hook is for the CLIENT ONLY");
        }
        this.blockLightMap.remove(CoordinateUtils.getChunkKey(chunkPos));
        this.skyLightMap.remove(CoordinateUtils.getChunkKey(chunkPos));
    }

    @Override
    public void clientChunkLoad(final ChunkPos pos, final Chunk chunk) {
        if (((Object)this).getClass() != WorldLightManager.class) {
            throw new IllegalStateException("This hook is for the CLIENT ONLY");
        }
        final long key = CoordinateUtils.getChunkKey(pos);
        final SWMRNibbleArray[] blockNibbles = this.blockLightMap.get(key);
        final SWMRNibbleArray[] skyNibbles = this.skyLightMap.get(key);
        if (blockNibbles != null) {
            ((ExtendedChunk)chunk).setBlockNibbles(blockNibbles);
        }
        if (skyNibbles != null) {
            ((ExtendedChunk)chunk).setSkyNibbles(skyNibbles);
        }
    }
}
