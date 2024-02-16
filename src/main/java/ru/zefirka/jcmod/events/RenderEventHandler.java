package ru.zefirka.jcmod.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.JCMod;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderEventHandler {
    @SubscribeEvent
    public static void onRender(RenderPlayerEvent event) {
        PlayerEntity entity = (PlayerEntity) event.getEntityLiving();
        AxisAlignedBB axisAlignedBB = entity.getBoundingBox();
    }
}
