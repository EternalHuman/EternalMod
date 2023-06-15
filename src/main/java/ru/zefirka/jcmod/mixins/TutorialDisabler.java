package ru.zefirka.jcmod.mixins;

import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tutorial.class)
public class TutorialDisabler {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        ci.cancel();
    }
}
