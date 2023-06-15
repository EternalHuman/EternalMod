package ru.zefirka.jcmod.mixins;

import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(IngameGui.class)
public abstract class InGameHudMixin {

    @ModifyVariable(method = "displayScoreboardSidebar", at = @At("LOAD"), ordinal = 0)
    private String injected(String s) {
        return "";
    }
}
