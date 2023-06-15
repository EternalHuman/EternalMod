package ru.zefirka.jcmod.updater;

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
import java.util.Map;
import java.util.function.Consumer;

public class UpdateFile {
    private final String id, fileName, path;
    private String originalFileSum, newFileSum;
    private File file;
    private final Map<UpdaterSource, String> updateUrls;
    private final boolean rebootClient;
    private Consumer<UpdateFile> afterUpdate;

    public UpdateFile(String id, String fileName, String path, Map<UpdaterSource, String> updateUrls, boolean rebootClient) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
        this.updateUrls = updateUrls;
        this.rebootClient = rebootClient;
    }

    public void init() {
        initFileMD5();
        initFile();
    }

    public Consumer<UpdateFile> getAfterUpdate() {
        return afterUpdate;
    }

    public void update() {
        try {
            StartupMessageManager.addModMessage("Updating: " + getFileName());
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
                JCMod.logger.error("Error while download file, change updater", e);
            }
        } catch (Exception e) {
            Updater.nextUpdater();
            update();
            JCMod.logger.error("Error while updating, change updater", e);
        }
    }

    public String getFullPath() {
        return getPath() + File.separator + getFileName();
    }

    public boolean isRebootClient() {
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

    public String getId() {
        return id;
    }

    public String getOriginalFileSum() {
        return originalFileSum;
    }

    public void setOriginalFileSum(String originalFileSum) {
        this.originalFileSum = originalFileSum;
    }

    public String getNewFileSum() {
        return newFileSum;
    }

    public void setNewFileSum(String newFileSum) {
        this.newFileSum = newFileSum;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public String getUpdateUrl() {
        return updateUrls.get(Updater.getCurrentUpdater());
    }
}
