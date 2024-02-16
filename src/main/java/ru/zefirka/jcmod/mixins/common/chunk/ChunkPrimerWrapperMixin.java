package ru.zefirka.jcmod.mixins.common.chunk;

import ru.zefirka.jcmod.lightoptimizer.common.chunk.ExtendedChunk;
import ru.zefirka.jcmod.lightoptimizer.common.light.SWMRNibbleArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.IChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkPrimerWrapper.class)
public abstract class ChunkPrimerWrapperMixin extends ChunkPrimer implements IChunk, ExtendedChunk {

    @Final
    @Shadow
    private Chunk wrapped;

    public ChunkPrimerWrapperMixin(final ChunkPos pos, final UpgradeData data) {
        super(pos, data);
    }

    @Override
    public SWMRNibbleArray[] getBlockNibbles() {
        return ((ExtendedChunk)this.wrapped).getBlockNibbles();
    }

    @Override
    public void setBlockNibbles(final SWMRNibbleArray[] nibbles) {
        ((ExtendedChunk)this.wrapped).setBlockNibbles(nibbles);
    }

    @Override
    public SWMRNibbleArray[] getSkyNibbles() {
        return ((ExtendedChunk)this.wrapped).getSkyNibbles();
    }

    @Override
    public void setSkyNibbles(final SWMRNibbleArray[] nibbles) {
        ((ExtendedChunk)this.wrapped).setSkyNibbles(nibbles);
    }

    @Override
    public boolean[] getSkyEmptinessMap() {
        return ((ExtendedChunk)this.wrapped).getSkyEmptinessMap();
    }

    @Override
    public void setSkyEmptinessMap(final boolean[] emptinessMap) {
        ((ExtendedChunk)this.wrapped).setSkyEmptinessMap(emptinessMap);
    }

    @Override
    public boolean[] getBlockEmptinessMap() {
        return ((ExtendedChunk)this.wrapped).getBlockEmptinessMap();
    }

    @Override
    public void setBlockEmptinessMap(final boolean[] emptinessMap) {
        ((ExtendedChunk)this.wrapped).setBlockEmptinessMap(emptinessMap);
    }
}
