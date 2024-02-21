package ru.zefirka.jcmod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.utils.ResourcePack;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenEventHandler {

    @SubscribeEvent
    public static void onEvent(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        Minecraft minecraft = JCMod.MINECRAFT;
        if (screen instanceof MainMenuScreen) {
            ResourcePack.updateRP(minecraft);
        }
    }
}