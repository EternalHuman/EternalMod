package ru.zefirka.jcmod.lightoptimizer.common.world;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import ru.zefirka.jcmod.lightoptimizer.common.light.VariableBlockLightHandler;

public interface ExtendedWorld {

    // rets full chunk without blocking
    public Chunk getChunkAtImmediately(final int chunkX, final int chunkZ);

    // rets chunk at any stage, if it exists, immediately
    public IChunk getAnyChunkImmediately(final int chunkX, final int chunkZ);

    public VariableBlockLightHandler getCustomLightHandler();

    public void setCustomLightHandler(final VariableBlockLightHandler handler);

}
