package ru.zefirka.jcmod.mixins;

import net.minecraft.client.gui.recipebook.RecipeBookGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookGui.class)
public class BookGuiDisabler {

    @Inject(method = "initVisuals", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "isVisible", at = @At("RETURN"), cancellable = true)
    private void isVisible(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
