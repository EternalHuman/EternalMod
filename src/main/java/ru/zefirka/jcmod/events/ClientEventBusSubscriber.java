package ru.zefirka.jcmod.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.zefirka.jcmod.binds.BindManager;
import ru.zefirka.jcmod.JCMod;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        BindManager.init();
        JCMod.INSTANCE.onInitialize();
    }
}
