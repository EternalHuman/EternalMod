package ru.zefirka.jcmod.mixins.common.chunk;

import ru.zefirka.jcmod.lightoptimizer.common.chunk.ExtendedChunk;
import ru.zefirka.jcmod.lightoptimizer.common.light.SWMRNibbleArray;
import ru.zefirka.jcmod.lightoptimizer.common.light.StarLightEngine;
import ru.zefirka.jcmod.lightoptimizer.common.util.WorldUtil;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EmptyChunk.class)
public abstract class EmptyChunkMixin extends Chunk implements IChunk, ExtendedChunk {

    public EmptyChunkMixin(final World world, final ChunkPrimer protoChunk) {
        super(world, protoChunk);
    }

    @Override
    public SWMRNibbleArray[] getBlockNibbles() {
        return StarLightEngine.getFilledEmptyLight(this.getLevel());
    }

    @Override
    public void setBlockNibbles(final SWMRNibbleArray[] nibbles) {}

    @Override
    public SWMRNibbleArray[] getSkyNibbles() {
        return StarLightEngine.getFilledEmptyLight(this.getLevel());
    }

    @Override
    public void setSkyNibbles(final SWMRNibbleArray[] nibbles) {}

    @Override
    public boolean[] getSkyEmptinessMap() {
        return null;
    }

    @Override
    public void setSkyEmptinessMap(final boolean[] emptinessMap) {}

    @Override
    public boolean[] getBlockEmptinessMap() {
        return null;
    }

    @Override
    public void setBlockEmptinessMap(final boolean[] emptinessMap) {}
}
