package ru.zefirka.jcmod.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.binds.BindManager;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyEventHandler
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        BindManager.onInput();
    }

    @SubscribeEvent
    public static void onMousePress(InputEvent.MouseInputEvent event) {
        BindManager.onInput();
    }
}