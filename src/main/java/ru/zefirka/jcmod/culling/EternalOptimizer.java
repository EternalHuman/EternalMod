package ru.zefirka.jcmod.culling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import ru.zefirka.jcmod.Provider;
import ru.zefirka.jcmod.config.ConfigUpgrader;
import ru.zefirka.jcmod.config.EternalModConfig;
import ru.zefirka.jcmod.updater.Updater;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public abstract class EternalOptimizer {
    public OcclusionCullingInstance culling, cullingTiles;

    public CullingTask cullingTask;
    private Thread cullThread;
    protected KeyBinding keybind = new KeyBinding("key.optimization.toggle", -1, "EternalOptimizer");
    private boolean pressed = false;
    private boolean lateInit = false;
    private static final List<EntityType> noCullingEntities = Arrays.asList(EntityType.ENDER_DRAGON, EntityType.LIGHTNING_BOLT, EntityType.FISHING_BOBBER);

    @Getter
    private static EternalModConfig eternalModConfig;
    private final File settingsFile = new File("config", "eternalmod.json");
    private final File legacyVersion = new File("mods", "entityculling-forge-mc1.16.5-1.5.2.jar");
    private final File legacyStarlight = new File("mods", "starlight-forge-1.0.0-RC2-1.16.5.jar");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void onInitialize() {
        if (legacyVersion.exists()) {
            legacyVersion.delete();
            Updater.REBOOT = true;
        }
        if (legacyStarlight.exists()) {
            legacyStarlight.delete();
            Updater.REBOOT = true;
        }

        if (settingsFile.exists()) {
            try {
                eternalModConfig = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        EternalModConfig.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (eternalModConfig == null) {
            eternalModConfig = new EternalModConfig();
            writeConfig();
        } else {
            if (ConfigUpgrader.upgradeConfig(eternalModConfig)) {
                writeConfig();
            }
        }
        culling = new OcclusionCullingInstance(eternalModConfig.cullingEntitiesDistance, new Provider());
        cullingTiles = new OcclusionCullingInstance(70, new Provider());
        cullingTask = new CullingTask(culling, cullingTiles, eternalModConfig);

        cullThread = new Thread(cullingTask, "CullThread");
        cullThread.setUncaughtExceptionHandler((thread, ex) -> {
            System.out.println("The CullingThread has crashed! Please report the following stacktrace!");
            ex.printStackTrace();
        });

        initModloader();
    }

    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(eternalModConfig).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void worldTick() {
        cullingTask.requestCull = true;
    }

    public void clientTick() {
        if (!lateInit) {
            lateInit = true;
            cullThread.start();
            for (String blockId : eternalModConfig.blockEntityWhitelist) {
                Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(blockId)).ifPresent(e -> ((CullableType) e).setCullWhitelisted(true));
            }
            for (String entityType : eternalModConfig.entityWhitelist) {
                Registry.ENTITY_TYPE.getOptional(new ResourceLocation(entityType)).ifPresent(e -> ((CullableType) e).setCullWhitelisted(true));
            }
            noCullingEntities.forEach(entityType -> {
                ((CullableType) entityType).setCullWhitelisted(true);
            });
        }
        if (keybind.isDown()) {
            if (pressed) return;
            pressed = true;
            cullingTask.disableBlockEntityCulling = !cullingTask.disableBlockEntityCulling;
            cullingTask.disableEntityCulling = !cullingTask.disableEntityCulling;
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                if (!cullingTask.disableEntityCulling) {
                    player.sendMessage(new StringTextComponent("Optimization enabled! Good :)").withStyle(TextFormatting.GREEN),
                            Util.NIL_UUID);
                } else {
                    player.sendMessage(new StringTextComponent("Optimization disabled! Bad :(").withStyle(TextFormatting.RED),
                            Util.NIL_UUID);
                    Minecraft client = Minecraft.getInstance();
                    client.level.entitiesForRendering().forEach(entity -> {
                        try {
                            if (client.player == entity) return;
                            Cullable cullable = (Cullable) entity;
                            cullable.setCulled(false);
                            cullable.setOffScreen(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    player.level.blockEntityList.forEach(entity -> {
                        try {
                            Cullable cullable = (Cullable) entity;
                            cullable.setCulled(false);
                            cullable.setOffScreen(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } else {
            pressed = false;
        }

        cullingTask.requestCull = true;
    }

    public abstract void initModloader();
}
