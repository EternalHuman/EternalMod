package ru.zefirka.jcmod.mixins.common.chunk;

import net.minecraft.world.chunk.IChunk;
import org.spongepowered.asm.mixin.Mixin;
import ru.zefirka.jcmod.lightoptimizer.common.chunk.ExtendedChunk;

@Mixin(IChunk.class)
public interface ChunkMixin extends ExtendedChunk {}