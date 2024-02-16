package ru.zefirka.jcmod.culling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import ru.zefirka.jcmod.config.Config;
import ru.zefirka.jcmod.config.ConfigUpgrader;
import ru.zefirka.jcmod.Provider;
import ru.zefirka.jcmod.updater.Updater;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class EternalOptimizer {
    @Getter
    private static EternalOptimizer instance;
    public OcclusionCullingInstance culling;
    public Set<TileEntityType<?>> blockEntityWhitelist = new HashSet<>();
    public Set<EntityType<?>> entityWhistelist = new HashSet<>();
    public static boolean enabled = true; // public static to make it faster for the jvm
    public CullingTask cullingTask;
    private Thread cullThread;
    protected KeyBinding keybind = new KeyBinding("key.optimization.toggle", -1, "EternalOptimizer");
    protected boolean pressed = false;
    private boolean lateInit = false;
    private Set<Function<TileEntity, Boolean>> dynamicBlockEntityWhitelist = new HashSet<>();
    private Set<Function<Entity, Boolean>> dynamicEntityWhitelist = new HashSet<>();

    public Config config;
    private final File settingsFile = new File("config", "entitycullingeternal.json");
    private final File legacyVersion = new File("mods", "entityculling-forge-mc1.16.5-1.5.2.jar");
    private final File legacyStarlight = new File("mods", "starlight-forge-1.0.0-RC2-1.16.5.jar");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void onInitialize() {
        instance = this;
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
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        } else {
            if (ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
        culling = new OcclusionCullingInstance(config.tracingDistance, new Provider());
        cullingTask = new CullingTask(culling, blockEntityWhitelist, entityWhistelist, this);

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
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void worldTick() {
        cullingTask.requestCull = true;
    }

    @SuppressWarnings("resource")
    public void clientTick() {
        if (!lateInit) {
            lateInit = true;
            cullThread.start();
            for (String blockId : config.blockEntityWhitelist) {
                Optional<TileEntityType<?>> block =  Registry.BLOCK_ENTITY_TYPE
                        .getOptional(new ResourceLocation(blockId));
                block.ifPresent(b -> {
                    blockEntityWhitelist.add(b);
                });
            }
            for (String entityType : config.tickCullingWhitelist) {
                Optional<EntityType<?>> entity =  Registry.ENTITY_TYPE
                        .getOptional(new ResourceLocation(entityType));
                entity.ifPresent(e -> {
                    entityWhistelist.add(e);
                });
            }
            for (String entityType : config.entityWhitelist) {
                Optional<EntityType<?>> entity =  Registry.ENTITY_TYPE
                        .getOptional(new ResourceLocation(entityType));
                entity.ifPresent(e -> {
                    entityWhistelist.add(e);
                });
            }
        }
        if (keybind.isDown()) {
            if (pressed)
                return;
            pressed = true;
            cullingTask.disableBlockEntityCulling = !cullingTask.disableBlockEntityCulling;
            enabled = !enabled;
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (!cullingTask.disableBlockEntityCulling) {
                if (player != null) {
                    player.sendMessage(new StringTextComponent("Optimization enabled! Good :)").withStyle(TextFormatting.GREEN),
                            Util.NIL_UUID);
                }
            } else {
                if (player != null) {
                    player.sendMessage(new StringTextComponent("Optimization disabled! Bad :(").withStyle(TextFormatting.RED),
                            Util.NIL_UUID);
                }
            }
        } else {
            pressed = false;
        }

        cullingTask.requestCull = true;
    }

    public abstract void initModloader();

    public abstract AxisAlignedBB setupAABB(TileEntity entity, BlockPos pos);

    public boolean isDynamicWhitelisted(TileEntity entity) {
        for (Function<TileEntity, Boolean> fun : dynamicBlockEntityWhitelist) {
            if (fun.apply(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDynamicWhitelisted(Entity entity) {
        for (Function<Entity, Boolean> fun : dynamicEntityWhitelist) {
            if (fun.apply(entity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a dynamic function that can return true to disable culling for a
     * BlockEntity temporarly.
     *
     * @param function
     */
    public void addDynamicBlockEntityWhitelist(Function<TileEntity, Boolean> function) {
        this.dynamicBlockEntityWhitelist.add(function);
    }

    /**
     * Add a dynamic function that can return true to disable culling for an entity
     * temporarly.
     *
     * @param function
     */
    public void addDynamicEntityWhitelist(Function<Entity, Boolean> function) {
        this.dynamicEntityWhitelist.add(function);
    }

}
