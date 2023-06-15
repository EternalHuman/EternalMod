package ru.zefirka.jcmod.updater;

import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.utils.BuilderEnumMap;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Updater {
    private static Map<String, UpdateFile> updateFiles = new HashMap<>();
    private static Map<String, String> newHashSums = new HashMap<>();
    private static Map<UpdaterSource, String> hashSumsUrls = BuilderEnumMap.stringsBuilder(UpdaterSource.class)
            .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/q9e948x2db80vf9/sums.txt?dl=1")
            .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/s/q9e948x2db80vf9/sums.txt?dl=1");
    private static String absolutePath;
    private static UpdaterSource currentUpdater;

    public static boolean REBOOT = false;

    public static void setAbsolutePath(String absolutePath) {
        Updater.absolutePath = absolutePath;
    }

    public static void init() {
        currentUpdater = UpdaterSource.DROPBOX;
        try {
            StartupMessageManager.addModMessage("UPDATES CHECK...");

            initHashSums();

            initFile(new UpdateFile("rp", "JediPack Eternal.zip", absolutePath + File.separator + "resourcepacks",
                    BuilderEnumMap.stringsBuilder(UpdaterSource.class)
                            .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/nk6af45cgs6ady8/JediPack%20Eternal.zip?dl=1")
                            .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/s/nk6af45cgs6ady8/JediPack%20Eternal.zip?dl=1")
                            .append(UpdaterSource.YANDEX_DISK, "")
                            .append(UpdaterSource.GOOGLE_DRIVE, "")
                            .append(UpdaterSource.ONE_DRIVE, "")
                    , false));

            initFile(new UpdateFile("ips", "servers.dat", absolutePath,
                    BuilderEnumMap.stringsBuilder(UpdaterSource.class)
                            .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/l600pueyz81e7jm/servers.dat?dl=1")
                            .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/s/l600pueyz81e7jm/servers.dat?dl=1")
                            .append(UpdaterSource.YANDEX_DISK, "")
                            .append(UpdaterSource.GOOGLE_DRIVE, "")
                            .append(UpdaterSource.ONE_DRIVE, "")
                    , false));

            initFile(new UpdateFile("mod", "JCMod-1.2-Final.jar", absolutePath + File.separator + "mods",
                    BuilderEnumMap.stringsBuilder(UpdaterSource.class)
                            .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/zoydenumhw7nd2q/JCMod-1.2-Final.jar?dl=1")
                            .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/s/zoydenumhw7nd2q/JCMod-1.2-Final.jar?dl=1")
                            .append(UpdaterSource.YANDEX_DISK, "")
                            .append(UpdaterSource.GOOGLE_DRIVE, "")
                            .append(UpdaterSource.ONE_DRIVE, "")
                    , true));

            startChecker();
            StartupMessageManager.addModMessage("UPDATED!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startChecker() {
        updateFiles.forEach((id, updateFile) -> {
            updateFile.setNewFileSum(newHashSums.get(id));
            if (updateFile.getNewFileSum().equals(updateFile.getOriginalFileSum())) {
                System.out.println("SIMILAR");
            } else {
                System.out.println("NOT SIMILAR, UPDATING...");
                updateFile.update();
                if (updateFile.isRebootClient()) {
                    REBOOT = true;
                    StartupMessageManager.addModMessage("Updated! Reboot client.");
                    System.out.println("Important update! Reboot client...");
                }
            }
        });
    }

    public static void initFile(UpdateFile updateFile) {
        updateFiles.put(updateFile.getId(), updateFile);
        updateFile.init();
    }

    public static void initHashSums() {
        if (currentUpdater == null) return;
        try {
            URL url = new URL(hashSumsUrls.get(currentUpdater));
            Scanner s = new Scanner(url.openStream());
            while (s.hasNext()) {
                String next = s.next();
                String[] splitted = next.split("=");
                newHashSums.put(splitted[0], splitted[1]);
            }
            System.out.println("All sums initialized!");
        } catch (Exception e) {
            nextUpdater();
            initHashSums();
            e.printStackTrace();
        }
    }

    protected static void nextUpdater() {
        if (UpdaterSource.values().length < currentUpdater.ordinal() + 2) {
            currentUpdater = null;
            throw new UnsupportedOperationException("Failed all updaters");
        }
        UpdaterSource nextSource = UpdaterSource.values()[currentUpdater.ordinal() + 1];
        JCMod.logger.info("Change updater from " + currentUpdater.name() + " to " + nextSource.name());
        currentUpdater = nextSource;
    }

    public static UpdaterSource getCurrentUpdater() {
        return currentUpdater;
    }
}
