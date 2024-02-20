package ru.zefirka.jcmod.utils.render;

import net.minecraft.client.renderer.model.ModelRenderer;

public interface ModelCuboidAccessor {
    ModelRenderer.TexturedQuad[] getQuads();
}
