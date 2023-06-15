package ru.zefirka.jcmod.esp;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.utils.RenderBlockProps;
import ru.zefirka.jcmod.utils.RenderUtils;
import java.awt.*;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = JCMod.MODID, value = Dist.CLIENT)
public class ChestLocator {
    public static long COOLDOWN = TimeUnit.SECONDS.toMicros(60);
    public static long DURATION = TimeUnit.SECONDS.toMicros(15);

    public static boolean enabled = false;
    private static long lastEspTime = -1;

    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event) {
        if (isEnabled()) runChestFinder();
    }

    @SubscribeEvent
    public static void chunkUnload(ChunkEvent.Unload event) {
        if (isEnabled()) runChestFinder();
    }

    @SubscribeEvent
    public static void onWorldRenderLast(RenderWorldLastEvent event) {
        if (isEnabled() && JCMod.MINECRAFT.player != null) RenderUtils.renderBlocks(event);
    }

    public static void runChestFinder() {
        RenderUtils.syncRenderList.clear();
        Minecraft minecraft = JCMod.MINECRAFT;
        if (minecraft.level == null) return;
        minecraft.level.blockEntityList.forEach(tileEntity -> {
            if (tileEntity.getType() != TileEntityType.CHEST && tileEntity.getType() != TileEntityType.TRAPPED_CHEST) return;
            RenderUtils.syncRenderList.add(new RenderBlockProps(tileEntity.getBlockPos(), Color.GREEN.getRGB()));
        });
    }

    public static long getLastEspTime() {
        return lastEspTime;
    }

    public static void setLastEspTime(long lastEspTime) {
        ChestLocator.lastEspTime = lastEspTime;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        if (!enabled) RenderUtils.syncRenderList.clear();
        ChestLocator.enabled = enabled;
    }
}
