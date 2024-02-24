package ru.zefirka.jcmod.mixins.perf.sprite;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zefirka.jcmod.utils.sprite.SpriteUtil;

@Mixin(AtlasTexture.class)
public class MixinSpriteAtlasTexture {
    @Inject(method = "getSprite", at = @At("RETURN"))
    private void preReturnSprite(CallbackInfoReturnable<TextureAtlasSprite> cir) {
        TextureAtlasSprite sprite = cir.getReturnValue();

        if (sprite != null) {
            SpriteUtil.markSpriteActive(sprite);
        }
    }
}
