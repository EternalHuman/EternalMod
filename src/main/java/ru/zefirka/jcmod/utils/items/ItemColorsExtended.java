package ru.zefirka.jcmod.utils.items;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public interface ItemColorsExtended {
    IItemColor getColorProvider(ItemStack stack);
}
