package ru.zefirka.jcmod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zefirka.jcmod.updater.Updater;
import java.io.IOException;

@Mod("jcmod")
public class JCMod
{
    public static final String MODID = "jcmod";

    public static final String VK_LINK = "https://vk.com/eternaljc";
    public static final String DS_LINK = "https://discord.gg/BYXs8TBeEG";

    public static String path;

    public static boolean DEBUG = false, ENABLED = true, UPDATER_TESTS = false;

    public static final Logger LOGGER = LogManager.getLogger();
    public static Minecraft MINECRAFT;

    public JCMod() {
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
    }

    public static void debug(String text) {
        if (!JCMod.DEBUG) return;
        if (MINECRAFT.player == null) return;
        MINECRAFT.player.sendMessage(new StringTextComponent(text), MINECRAFT.player.getUUID());
    }
}
