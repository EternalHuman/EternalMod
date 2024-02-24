package ru.zefirka.jcmod.mixins.perf.sprite;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.utils.sprite.SpriteUtil;

@Mixin(SpriteTexturedParticle.class)
public abstract class MixinSpriteBillboardParticle extends TexturedParticle {
    @Shadow
    protected TextureAtlasSprite sprite;

    private boolean shouldTickSprite;

    protected MixinSpriteBillboardParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Inject(method = "setSprite", at = @At("RETURN"))
    private void afterSetSprite(TextureAtlasSprite sprite, CallbackInfo ci) {
        this.shouldTickSprite = sprite != null && sprite.isAnimation();
    }

    @Override
    public void render(IVertexBuilder vertexConsumer, ActiveRenderInfo camera, float tickDelta) {
        if (this.shouldTickSprite) {
            SpriteUtil.markSpriteActive(this.sprite);
        }

        super.render(vertexConsumer, camera, tickDelta);
    }
}