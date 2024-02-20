package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.utils.render.ModelCuboidAccessor;

@Mixin(ModelRenderer.ModelBox.class)
public class MixinCuboid implements ModelCuboidAccessor {
    @Shadow
    @Final
    private ModelRenderer.TexturedQuad[] polygons;

    @Override
    public ModelRenderer.TexturedQuad[] getQuads() {
        return this.polygons;
    }
}
