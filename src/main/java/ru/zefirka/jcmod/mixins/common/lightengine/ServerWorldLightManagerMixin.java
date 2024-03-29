package ru.zefirka.jcmod.mixins.common.lightengine;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightEngine;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightInterface;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightLightingProvider;
import ru.zefirka.jcmod.lightoptimizer.common.util.CoordinateUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(ServerWorldLightManager.class)
public abstract class ServerWorldLightManagerMixin extends WorldLightManager implements StarLightLightingProvider {

    @Final
    @Shadow
    private static Logger LOGGER;

    @Shadow @Final private ChunkManager chunkMap;

    @Shadow public abstract void tryScheduleUpdate();

    public ServerWorldLightManagerMixin(final IChunkLightProvider chunkProvider, final boolean hasBlockLight, final boolean hasSkyLight) {
        super(chunkProvider, hasBlockLight, hasSkyLight);
    }


    @Unique
    private final Long2IntOpenHashMap chunksBeingWorkedOn = new Long2IntOpenHashMap();

    @Unique
    private void queueTaskForSection(final int chunkX, final int chunkY, final int chunkZ,
                                     final Supplier<StarLightInterface.LightQueue.ChunkTasks> runnable) {
        final ServerWorld world = (ServerWorld)this.getLightEngine().getWorld();

        final IChunk center = this.getLightEngine().getAnyChunkNow(chunkX, chunkZ);
        if (center == null || !center.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            // do not accept updates in unlit chunks, unless we might be generating a chunk. thanks to the amazing
            // chunk scheduling, we could be lighting and generating a chunk at the same time
            return;
        }

        if (center.getStatus() != ChunkStatus.FULL) {
            // do not keep chunk loaded, we are probably in a gen thread
            // if we proceed to add a ticket the chunk will be loaded, which is not what we want (avoid cascading gen)
            runnable.get();
            return;
        }

        if (!world.getChunkSource().chunkMap.mainThreadExecutor.isSameThread()) {
            // ticket logic is not safe to run off-main, re-schedule
            world.getChunkSource().chunkMap.mainThreadExecutor.execute(() -> {
                this.queueTaskForSection(chunkX, chunkY, chunkZ, runnable);
            });
            return;
        }

        final long key = CoordinateUtils.getChunkKey(chunkX, chunkZ);

        final StarLightInterface.LightQueue.ChunkTasks updateFuture = runnable.get();

        if (updateFuture == null) {
            // not scheduled
            return;
        }

        if (updateFuture.isTicketAdded) {
            // ticket already added
            return;
        }
        updateFuture.isTicketAdded = true;

        final int references = this.chunksBeingWorkedOn.addTo(key, 1);
        if (references == 0) {
            final ChunkPos pos = new ChunkPos(chunkX, chunkZ);
            world.getChunkSource().registerTickingTicket(StarLightInterface.CHUNK_WORK_TICKET, pos, 0, pos);
        }

        updateFuture.onComplete.thenAcceptAsync((final Void ignore) -> {
            final int newReferences = this.chunksBeingWorkedOn.get(key);
            if (newReferences == 1) {
                this.chunksBeingWorkedOn.remove(key);
                final ChunkPos pos = new ChunkPos(chunkX, chunkZ);
                world.getChunkSource().releaseTickingTicket(StarLightInterface.CHUNK_WORK_TICKET, pos, 0, pos);
            } else {
                this.chunksBeingWorkedOn.put(key, newReferences - 1);
            }
        }, world.getChunkSource().chunkMap.mainThreadExecutor).whenComplete((final Void ignore, final Throwable thr) -> {
            if (thr != null) {
                LOGGER.fatal("Failed to remove ticket level for post chunk task " + new ChunkPos(chunkX, chunkZ), thr);
            }
        });
    }

    /**
     * @reason Redirect scheduling call away from the vanilla light engine, as well as enforce
     * that chunk neighbours are loaded before the processing can occur
     * @author Spottedleaf
     */
    @Overwrite
    public void checkBlock(final BlockPos pos) {
        final BlockPos posCopy = pos.immutable();
        this.queueTaskForSection(posCopy.getX() >> 4, posCopy.getY() >> 4, posCopy.getZ() >> 4, () -> {
            return this.getLightEngine().blockChange(posCopy);
        });
    }

    /**
     * @reason Avoid messing with the vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void updateChunkStatus(final ChunkPos pos) {}

    /**
     * @reason Redirect to schedule for our own logic, as well as ensure 1 radius neighbours
     * are loaded
     * Note: Our scheduling logic will discard this call if the chunk is not lit, unloaded, or not at LIGHT stage yet.
     * @author Spottedleaf
     */
    @Overwrite
    public void updateSectionStatus(final SectionPos pos, final boolean notReady) {
        this.queueTaskForSection(pos.getX(), pos.getY(), pos.getZ(), () -> {
            return this.getLightEngine().sectionChange(pos, notReady);
        });
    }

    /**
     * @reason Avoid messing with the vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void enableLightSources(final ChunkPos pos, final boolean lightEnabled) {
        // light impl does not need to do this
    }

    /**
     * @reason Light data is now attached to chunks, and this means we need to hook into chunk loading logic
     * to load the data rather than rely on this call. This call also would mess with the vanilla light engine state.
     * @author Spottedleaf
     */
    @Overwrite
    public void queueSectionData(final LightType lightType, final SectionPos pos, final NibbleArray nibbles,
                        final boolean trustEdges) {
        // load hooks inside ChunkSerializer
    }

    /**
     * @reason Avoid messing with the vanilla light engine state
     * @author Spottedleaf
     */
    @Overwrite
    public void retainData(final ChunkPos pos, final boolean retainData) {
        // light impl does not need to do this
    }

    /**
     * @reason Route to new logic to either light or just load the data
     * @author Spottedleaf
     */
    @Overwrite
    public CompletableFuture<IChunk> lightChunk(final IChunk chunk, final boolean lit) {
        final ChunkPos chunkPos = chunk.getPos();

        return CompletableFuture.supplyAsync(() -> {
            final Boolean[] emptySections = StarLightEngine.getEmptySectionsForChunk(chunk);
            if (!lit) {
                chunk.setLightCorrect(false);
                this.getLightEngine().lightChunk(chunk, emptySections);
                chunk.setLightCorrect(true);
            } else {
                this.getLightEngine().forceLoadInChunk(chunk, emptySections);
                // can't really force the chunk to be edged checked, as we need neighbouring chunks - but we don't have
                // them, so if it's not loaded then i guess we can't do edge checks. later loads of the chunk should
                // catch what we miss here.
                this.getLightEngine().checkChunkEdges(chunkPos.x, chunkPos.z);
            }

            this.chunkMap.releaseLightTicket(chunkPos); // releaseLightTicket
            return chunk;
        }, (runnable) -> {
            this.getLightEngine().scheduleChunkLight(chunkPos, runnable);
            this.tryScheduleUpdate(); // tryScheduleUpdate
        }).whenComplete((final IChunk c, final Throwable throwable) -> {
            if (throwable != null) {
                LOGGER.fatal("Failed to light chunk " + chunkPos, throwable);
            }
        });
    }
}
