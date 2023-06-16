package ru.zefirka.jcmod.updater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import ru.zefirka.jcmod.JCMod;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Builder
public class UpdateFile {
    @Getter @Setter
    private String id, fileName, path, originalFileSum, newFileSum;
    private File file;
    private final Map<UpdaterSource, String> updateUrls;
    private final boolean rebootClient;
    private Consumer<UpdateFile> afterUpdate;

    protected void init() {
        initFileMD5();
        initFile();
    }

    protected Consumer<UpdateFile> getAfterUpdate() {
        return afterUpdate;
    }

    public void update() {
        try {
            StartupMessageManager.addModMessage("Updating: " + getFileName());
            if (this.file != null) {
                Files.deleteIfExists(this.file.toPath());
                System.out.println("Old " + getId() + " file deleted!");
            }
            long startMs = System.currentTimeMillis();
            URL website = new URL(getUpdateUrl());
            URLConnection urlConnection = website.openConnection();
            ReadableByteChannel rbc = Channels.newChannel(urlConnection.getInputStream());
            try (FileOutputStream fos = new FileOutputStream(getFullPath())) {
                FileChannel fileChannel = fos.getChannel();
                fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
                long endMs = System.currentTimeMillis();
                System.out.println("File " + getFileName() + " updated successfully! (" + (endMs - startMs) + " ms.)");
                if (getAfterUpdate() != null) getAfterUpdate().accept(this);
            } catch (Exception e) {
                Updater.nextUpdater();
                update();
                JCMod.LOGGER.error("Error while download file, change updater", e);
            }
        } catch (Exception e) {
            Updater.nextUpdater();
            update();
            JCMod.LOGGER.error("Error while updating, change updater", e);
        }
    }

    protected String getFullPath() {
        return getPath() + File.separator + getFileName();
    }

    protected boolean isRebootClient() {
        return rebootClient;
    }

    private void initFile() {
        this.file = new File(getFullPath());
    }

    private void initFileMD5() {
        try {
            byte[] data = Files.readAllBytes(Paths.get(getFullPath()));
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            String checksum = new BigInteger(1, hash).toString(16);
            setOriginalFileSum(checksum);
        } catch (Exception e) {
            setOriginalFileSum("null");
        }
    }

    protected String getUpdateUrl() {
        return updateUrls.get(Updater.getCurrentUpdater());
    }

    public static class UpdaterTest {
        public static void start() {
            Map<UpdateFile, String> hashSums = new HashMap<>();
            String path = JCMod.path + File.separator + "/tests";
            AtomicInteger tests = new AtomicInteger();
            for (UpdateFile updateFile : Updater.updateFiles.values()) {
                String hashSum = null;
                for (UpdaterSource updaterSource : UpdaterSource.values()) {
                    update(path, updateFile, updaterSource);
                    updateFile.init();
                    if (hashSum != null && !hashSum.equals(updateFile.newFileSum)) {
                        System.out.println("ERROR!");
                        System.out.println("Error with hashsums: " + updateFile.getId() + " " + updaterSource.name());
                        System.out.println("ERROR!");
                        return;
                    } else {
                        hashSum = updateFile.newFileSum;
                        System.out.println("Similar for " + updateFile.getId() + " " + updaterSource.name() + " md5: " + hashSum);
                        tests.incrementAndGet();
                    }
                }
                hashSums.put(updateFile, hashSum);
            }
            AtomicBoolean result = new AtomicBoolean(true);
            hashSums.forEach((file, nowHash) -> {
                String txtHash = Updater.newHashSums.get(file.getId());
                if (!txtHash.equals(nowHash)) {
                    System.out.println("ERROR!");
                    System.out.println("Error with sums.txt: " + file.getId());
                    System.out.println("Now: " + nowHash + " .txt: " + txtHash);
                    System.out.println("ERROR!");
                    result.set(false);
                } else {
                    System.out.println("Test for " + file.getId() + " passed!");
                    tests.incrementAndGet();
                }
            });
            if (result.get()) {
                System.out.println("----------------------");
                System.out.println("all " + tests.get() + " tests have been passed!");
                System.out.println("----------------------");
            }
        }

        public static void update(String path, UpdateFile updateFile, UpdaterSource updaterSource) {
            try {
                URL website = new URL(updateFile.updateUrls.get(updaterSource));
                URLConnection urlConnection = website.openConnection();
                ReadableByteChannel rbc = Channels.newChannel(urlConnection.getInputStream());
                try (FileOutputStream fos = new FileOutputStream(path + File.separator + updateFile.getFileName() + updaterSource.ordinal())) {
                    FileChannel fileChannel = fos.getChannel();
                    fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
