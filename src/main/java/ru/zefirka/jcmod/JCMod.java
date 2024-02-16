package ru.zefirka.jcmod;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zefirka.jcmod.culling.EternalOptimizer;
import ru.zefirka.jcmod.updater.Updater;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;

@Mod("jcmod")
public class JCMod extends EternalOptimizer {
    public static final String MODID = "jcmod";
    public static JCMod INSTANCE;

    public static final String VK_LINK = "https://vk.com/eternaljc";
    public static final String DS_LINK = "https://discord.gg/BYXs8TBeEG";

    public static String path;

    public static boolean DEBUG = false, ENABLED = true, UPDATER_TESTS = false;

    public static final Logger LOGGER = LogManager.getLogger();
    public static Minecraft MINECRAFT;

    public JCMod() {
        INSTANCE = this;
        MINECRAFT = Minecraft.getInstance();
        try {
            path = MINECRAFT.gameDirectory.getCanonicalPath();
            Updater.setAbsolutePath(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MINECRAFT.options.realmsNotifications = false;
        MINECRAFT.options.skipMultiplayerWarning = true;
        Updater.init();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    public static void debug(String text) {
        if (!JCMod.DEBUG) return;
        if (MINECRAFT.player == null) return;
        MINECRAFT.player.sendMessage(new StringTextComponent(text), MINECRAFT.player.getUUID());
    }

    public void initModloader() {
        ClientRegistry.registerKeyBinding(this.keybind);
        MinecraftForge.EVENT_BUS.addListener(this::doClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::doWorldTick);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    private void doClientTick(TickEvent.ClientTickEvent event) {
        clientTick();
    }

    private void doWorldTick(TickEvent.WorldTickEvent event) {
        worldTick();
    }

    public AxisAlignedBB setupAABB(TileEntity entity, BlockPos pos) {
        return entity.getRenderBoundingBox();
    }

}
