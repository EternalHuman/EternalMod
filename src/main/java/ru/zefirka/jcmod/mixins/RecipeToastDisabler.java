package ru.zefirka.jcmod.mixins;

import net.minecraft.client.gui.toasts.ToastGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastGui.class)
public class RecipeToastDisabler {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        ci.cancel();
    }
}
