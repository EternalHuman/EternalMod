package ru.zefirka.jcmod.mixins.screens;

import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {

    @Redirect(method = "init", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/inventory/InventoryScreen;addButton(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"))
    public Widget addButton(InventoryScreen instance, Widget widget) {
        return null;
    }
}
