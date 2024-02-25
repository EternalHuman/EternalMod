package ru.zefirka.jcmod.utils.baked;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.IForgeBakedModel;

public abstract class DynamicBakedModel implements IBakedModel {
    private final IBakedModel defaultModel;

    protected DynamicBakedModel(IBakedModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.defaultModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.defaultModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.defaultModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.defaultModel.getParticleIcon();
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return this.defaultModel.getTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.defaultModel.getOverrides();
    }
}
