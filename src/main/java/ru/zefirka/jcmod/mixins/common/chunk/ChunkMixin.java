package ru.zefirka.jcmod.mixins.common.chunk;

import ru.zefirka.jcmod.lightoptimizer.common.chunk.ExtendedChunk;
import net.minecraft.world.chunk.IChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IChunk.class)
public interface ChunkMixin extends ExtendedChunk {}