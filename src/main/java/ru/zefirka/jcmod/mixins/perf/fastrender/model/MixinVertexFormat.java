package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.utils.vertex.BufferVertexFormat;

@Mixin(VertexFormat.class)
public abstract class MixinVertexFormat implements BufferVertexFormat {
    @Shadow
    public abstract int getVertexSize();

    @Override
    public int getStride() {
        return this.getVertexSize();
    }
}
