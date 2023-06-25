package ru.zefirka.jcmod.mixins;

import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PackScreen.class)
public class PackScreenChanger {
    @Shadow
    @Mutable
    private static final ITextComponent DRAG_AND_DROP = new TranslationTextComponent("cpack.dropInfo");
}
