package ru.zefirka.jcmod.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.utils.RenderUtils;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderEventHandler {
    @SubscribeEvent
    public static void worldUnload(WorldEvent.Unload event) {
        RenderUtils.clearCache();
    }
}
