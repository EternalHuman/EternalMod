package ru.zefirka.jcmod.binds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.esp.ChestLocator;

import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class BindManager
{
    private static final Map<String, CustomBind> customBindMap = new HashMap<>();
    private static final Set<KeyBinding> keybindsMap = new HashSet<>();
    public static final String CATEGORY = "jcmod.category";

    public static void init() {
        createBind("key.bind.cl", clientPlayerEntity -> {
            long endTime = ChestLocator.getLastEspTime() + ChestLocator.COOLDOWN;
            long nowTime = System.currentTimeMillis();
            if (nowTime < endTime) {
                int leftSeconds = (int) ((endTime - nowTime) / 1000);
                clientPlayerEntity.sendMessage(new TranslationTextComponent("eternalmod.cl.cooldown", leftSeconds),
                        clientPlayerEntity.getUUID());
                return;
            }
            ChestLocator.runChestFinder();
            ChestLocator.setLastEspTime(nowTime);
            ChestLocator.setEnabled(true);
            clientPlayerEntity.sendMessage(new TranslationTextComponent("eternalmod.cl.enabled", ChestLocator.DURATION / 1000),
                    clientPlayerEntity.getUUID());
            clientPlayerEntity.chat("/tutorial esp");
            new Timer().schedule((
                    new TimerTask() {
                        @Override
                        public void run() {
                            ChestLocator.setEnabled(false);
                            clientPlayerEntity.sendMessage(new TranslationTextComponent("eternalmod.cl.disabled",
                                            (ChestLocator.COOLDOWN - ChestLocator.DURATION) / 1000),
                                    clientPlayerEntity.getUUID());
                        }
                    }),
                    ChestLocator.DURATION
            );
        }, GLFW.GLFW_KEY_F6, InputMappings.Type.KEYSYM);

        createBind("key.bind.ability1a", "/keybind 1 -c", GLFW.GLFW_KEY_Z);
        createBind("key.bind.ability1b", "/keybind 2 -c", GLFW.GLFW_KEY_X);
        createBind("key.bind.ability1c", "/keybind 3 -c", GLFW.GLFW_KEY_C);
        createBind("key.bind.ability1d", "/keybind 4 -c", GLFW.GLFW_KEY_R);
        createBind("key.bind.ability1e", "/keybind 5 -c", GLFW.GLFW_KEY_F4);
        createBind("key.bind.ability1f", "/keybind 6 -c", GLFW.GLFW_KEY_V);
        createBind("key.bind.ability1g", "/keybind 7 -c", GLFW.GLFW_KEY_U);
        createBind("key.bind.ability1h", "/keybind 8 -c", GLFW.GLFW_KEY_G);
        createBind("key.bind.ability1i", "/keybind 9 -c", GLFW.GLFW_KEY_B);
        createBind("key.bind.ability1j", "/keybind 10 -c", GLFW.GLFW_KEY_Y);
        createBind("key.bind.ability1k", "/keybind 11 -c", GLFW.GLFW_KEY_H);
        createBind("key.bind.ability1l", "/keybind 12 -c", GLFW.GLFW_KEY_N);
        createBind("key.bind.ability1m", "/keybind 13 -c", GLFW.GLFW_KEY_LEFT_ALT);
        createBind("key.bind.ability1n", "/keybind 14 -c", GLFW.GLFW_MOUSE_BUTTON_4, InputMappings.Type.MOUSE);
        createBind("key.bind.ability1o", "/keybind 15 -c", GLFW.GLFW_MOUSE_BUTTON_5, InputMappings.Type.MOUSE);

        createBind("key.bind.grenade1a", "/keybind 17 -c", GLFW.GLFW_MOUSE_BUTTON_6, InputMappings.Type.MOUSE);
        createBind("key.bind.grenade1b", "/keybind 18 -c",  GLFW.GLFW_KEY_O);
        createBind("key.bind.grenade1c", "/keybind 19 -c", GLFW.GLFW_KEY_CAPS_LOCK);
        createBind("key.bind.grenade1d", "/keybind 20 -c", GLFW.GLFW_KEY_F1);

        createBind("key.bind.debug", entityPlayer -> {
            JCMod.DEBUG = !JCMod.DEBUG;
            entityPlayer.sendMessage(new StringTextComponent("Debug mode" + (JCMod.DEBUG ? " enabled" : " disabled")), entityPlayer.getUUID());
        },  GLFW.GLFW_KEY_F8, InputMappings.Type.KEYSYM);
        createBind("key.bind.disable", entityPlayer -> {
            JCMod.ENABLED = !JCMod.ENABLED;
            entityPlayer.sendMessage(new StringTextComponent("Binds" + (JCMod.ENABLED ? " enabled" : " disabled")), entityPlayer.getUUID());
        },  GLFW.GLFW_KEY_F9, InputMappings.Type.KEYSYM);
    }

    private static void createBind(String techName, String command, int key, InputMappings.Type type) {
        createBind(techName, command, null, key, type);
    }

    private static void createBind(String techName, String command, int key) {
        createBind(techName, command, null, key, InputMappings.Type.KEYSYM);
    }

    private static void createBind(String techName, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        createBind(techName, null, consumer, key, type);
    }

    private static void createBind(String techName, String command, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        CustomBind customBind = new CustomBind(techName, command, consumer, key, type);
        customBindMap.put(customBind.getTechName(), customBind);
        keybindsMap.add(customBind.getKey());
    }
    public static CustomBind getCustomBind(String techName) {
        return customBindMap.getOrDefault(techName, null);
    }

    public static void onInput() {
        Minecraft minecraft = JCMod.MINECRAFT;
        if (!JCMod.ENABLED) return;
        if (minecraft.screen != null) return;
        for (KeyBinding keyBinding : keybindsMap) {
            if (!keyBinding.isDown()) continue;
            CustomBind customBind = BindManager.getCustomBind(keyBinding.getName());
            if (customBind == null) continue;
            JCMod.debug("M #4 SD NOT NULL");
            if (customBind.getConsumer() != null) {
                customBind.getConsumer().accept(minecraft.player);
            } else {
                minecraft.player.chat(customBind.getCommand());
            }
        }
    }
}
