package ru.zefirka.jcmod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zefirka.jcmod.culling.EternalOptimizer;
import ru.zefirka.jcmod.updater.Updater;

import java.io.IOException;

@Mod("jcmod")
public class JCMod extends EternalOptimizer {
    public static final String MODID = "jcmod";
    public static final String VERSION = "2.3.0";
    public static JCMod INSTANCE;

    public static final String VK_LINK = "https://vk.com/eternaljc";
    public static final String DS_LINK = "https://discord.gg/BYXs8TBeEG";

    public static String path;

    public static boolean DEBUG = false, ENABLED = true, UPDATER_TESTS = false;

    public static final Logger LOGGER = LogManager.getLogger();
    public static Minecraft MINECRAFT;

    public JCMod() {
        System.out.println("Starting EternalMod v" + VERSION);
        INSTANCE = this;
        MINECRAFT = Minecraft.getInstance();
        try {
            path = MINECRAFT.gameDirectory.getCanonicalPath();
            Updater.setAbsolutePath(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MINECRAFT.options.realmsNotifications = false;
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
    }

    private void doClientTick(TickEvent.ClientTickEvent event) {
        clientTick();
    }

    private void doWorldTick(TickEvent.WorldTickEvent event) {
        worldTick();
    }
}
