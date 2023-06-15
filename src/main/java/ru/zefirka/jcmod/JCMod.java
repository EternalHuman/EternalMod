package ru.zefirka.jcmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zefirka.jcmod.updater.Updater;

@Mod("jcmod")
public class JCMod
{
    public static final String MODID = "jcmod";
    public static final String NAME = "JCMod";
    public static final String VERSION = "1.3";

    public static final String VK_LINK = "https://vk.com/eternaljc";
    public static final String DS_LINK = "https://discord.gg/BYXs8TBeEG";

    public static String path;

    public static boolean DEBUG = false, ENABLED = true;

    public static Logger logger = LogManager.getLogger();
    public static Minecraft MINECRAFT;

    public JCMod() {
        try {
            MINECRAFT = Minecraft.getInstance();
            path = MINECRAFT.gameDirectory.getCanonicalPath();
            Updater.setAbsolutePath(path);
            MINECRAFT.options.realmsNotifications = false;
            MINECRAFT.options.skipMultiplayerWarning = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Updater.init();
    }
}
