package ru.zefirka.jcmod.mixins.perf.signs;

import net.minecraft.block.WoodType;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.RenderMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Atlases.class)
public class MixinTexturedRenderLayers {
    @Shadow
    @Final
    public static Map<WoodType, RenderMaterial> SIGN_MATERIALS;

    // WoodType -> RenderMaterial cache but for some reason doesn't use it.
    @Inject(method = "signTexture", at = @At("HEAD"), cancellable = true)
    private static void signTexture(WoodType type, CallbackInfoReturnable<RenderMaterial> ci) {
        if (SIGN_MATERIALS != null) {
            RenderMaterial sprite = SIGN_MATERIALS.get(type);

            if (type != null) {
                ci.setReturnValue(sprite);
            }
        }
    }
}
