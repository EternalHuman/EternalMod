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
    protected static final Map<String, UpdateFile> updateFiles = new HashMap<>();

    protected static final Map<String, String> newHashSums = new HashMap<>();
    private static final Map<UpdaterSource, String> hashSumsUrls = BuilderEnumMap.stringsBuilder(UpdaterSource.class)
            .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/q9e948x2db80vf9/sums.txt?dl=1")
            .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/scl/fi/vfka4g8pnq1p6b5z2332c/sums.txt?dl=1&rlkey=loeipadv7ksiy866w0zhaigtr")
            .append(UpdaterSource.GITHUB, "https://github.com/EternalHuman/EternalMod/releases/download/server/sums.txt")
            .append(UpdaterSource.YANDEX_DISK, "https://getfile.dokpub.com/yandex/get/https://disk.yandex.ru/d/ViAMs6kL3s4XMA")
            .append(UpdaterSource.GOOGLE_DRIVE, "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1uET9sHxEGCogttDXUMzDNwUspU56fXa_")
            .append(UpdaterSource.ONE_DRIVE, "https://onedrive.live.com/download?cid=CE8924A0FDC7D9EB&resid=CE8924A0FDC7D9EB%21806&authkey=ALhOGufe0Jl6Mmo");
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

            initFile(UpdateFile.builder().id("rp")
                    .fileName("JediPack.Eternal.zip")
                    .path(absolutePath + File.separator + "resourcepacks")
                    .updateUrls(BuilderEnumMap.stringsBuilder(UpdaterSource.class)
                            .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/zjdkuhx52rjhhnx/JediPack.Eternal.zip?dl=1")
                            .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/scl/fi/o1aa6g7zgrsxovnnho84l/JediPack.Eternal.zip?dl=1&rlkey=g2hqgzrdomiko4n2yept5uj7j")
                            .append(UpdaterSource.GITHUB, "https://github.com/EternalHuman/EternalMod/releases/download/server/JediPack.Eternal.zip")
                            .append(UpdaterSource.YANDEX_DISK, "https://getfile.dokpub.com/yandex/get/https://disk.yandex.ru/d/_MMdPKfmEgZ7mg")
                            .append(UpdaterSource.GOOGLE_DRIVE, "https://drive.google.com/uc?export=download&id=1cMC5I4ibGTvMux0bL6FCrjdtfnhGVwLk")
                            .append(UpdaterSource.ONE_DRIVE, "https://onedrive.live.com/download?cid=CE8924A0FDC7D9EB&resid=CE8924A0FDC7D9EB%21810&authkey=ACNewfxSr4hWHI8"))
                    .build());

            initFile(UpdateFile.builder().id("ips").fileName("servers.dat")
                    .path(absolutePath)
                    .updateUrls(
                            BuilderEnumMap.stringsBuilder(UpdaterSource.class)
                                    .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/l600pueyz81e7jm/servers.dat?dl=1")
                                    .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/scl/fi/yfwykitqhs4txwrankokp/servers.dat?dl=1&rlkey=t3o8f4rmmnsmmalnalrz5kv3n")
                                    .append(UpdaterSource.GITHUB, "https://github.com/EternalHuman/EternalMod/releases/download/server/servers.dat")
                                    .append(UpdaterSource.YANDEX_DISK, "https://getfile.dokpub.com/yandex/get/https://disk.yandex.ru/d/VdtsVuajuOexfw")
                                    .append(UpdaterSource.GOOGLE_DRIVE, "https://drive.google.com/uc?export=download&id=1szuFQt_4GLhQ_hV6pu0SQBhUNIWrLdpc")
                                    .append(UpdaterSource.ONE_DRIVE, "https://onedrive.live.com/download?cid=CE8924A0FDC7D9EB&resid=CE8924A0FDC7D9EB%21807&authkey=ADr1CCPiG7KQ6mc"))
                    .build());

            initFile(UpdateFile.builder()
                    .id("mod")
                    .fileName("EternalMod-STABLE.jar")
                    .path(absolutePath + File.separator + "mods")
                    .updateUrls(
                            BuilderEnumMap.stringsBuilder(UpdaterSource.class)
                                    .append(UpdaterSource.DROPBOX, "https://www.dropbox.com/s/x3t731ndh5dbahh/EternalMod-STABLE.jar?dl=1")
                                    .append(UpdaterSource.DROPBOX_RESERVE1, "https://www.dropbox.com/scl/fi/vrckrda7eytz4gu03ae58/EternalMod-STABLE.jar?dl=1&rlkey=78ayhv5hceb89s4zi40wi88iq")
                                    .append(UpdaterSource.GITHUB, "https://github.com/EternalHuman/EternalMod/releases/download/release/EternalMod-STABLE.jar")
                                    .append(UpdaterSource.YANDEX_DISK, "https://getfile.dokpub.com/yandex/get/https://disk.yandex.ru/d/A-09nx0ZCtmW0A")
                                    .append(UpdaterSource.GOOGLE_DRIVE, "https://drive.google.com/uc?export=download&id=14UYDk0Gx1Hn4oC8srqX2neHO0rlLXCqA")
                                    .append(UpdaterSource.ONE_DRIVE, "https://onedrive.live.com/download?cid=CE8924A0FDC7D9EB&resid=CE8924A0FDC7D9EB%21808&authkey=ALvCLnPcFBdCg38"))
                    .rebootClient(true)
                    .build());

            startChecker();
            StartupMessageManager.addModMessage("UPDATED!");
            if (JCMod.DEBUG && JCMod.UPDATER_TESTS) {
                UpdateFile.UpdaterTest.start(); //!!!ONLY FOR TESTS!!!
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startChecker() {
        updateFiles.forEach((id, updateFile) -> {
            updateFile.setNewFileSum(newHashSums.get(id));
            if (updateFile.getNewFileSum().equals(updateFile.getOriginalFileSum())) {
                System.out.println(getCurrentUpdater() + ": " + id + " SIMILAR");
            } else {
                System.out.println(getCurrentUpdater() + ": NOT SIMILAR, UPDATING...");
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
        JCMod.LOGGER.info("Change updater from " + currentUpdater.name() + " to " + nextSource.name());
        currentUpdater = nextSource;
    }

    public static UpdaterSource getCurrentUpdater() {
        return currentUpdater;
    }
}
