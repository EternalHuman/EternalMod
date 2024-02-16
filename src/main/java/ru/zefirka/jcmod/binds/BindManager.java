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
    private static long confirmTime = -1;

    public static void init() {
        createKeyBind("key.bind.cl", clientPlayerEntity -> {
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
                    ChestLocator.DURATION);
        }, GLFW.GLFW_KEY_F6);

        createKeyBind("key.bind.compass", "/keybind 101 -c", GLFW.GLFW_KEY_F7);

        createKeyBind("key.bind.ability1a", "/keybind 1 -c", GLFW.GLFW_KEY_Z);
        createKeyBind("key.bind.ability1b", "/keybind 2 -c", GLFW.GLFW_KEY_X);
        createKeyBind("key.bind.ability1c", "/keybind 3 -c", GLFW.GLFW_KEY_C);
        createKeyBind("key.bind.ability1d", "/keybind 4 -c", GLFW.GLFW_KEY_R);
        createKeyBind("key.bind.ability1e", "/keybind 5 -c", GLFW.GLFW_KEY_F4);
        createKeyBind("key.bind.ability1f", "/keybind 6 -c", GLFW.GLFW_KEY_V);
        createKeyBind("key.bind.ability1g", "/keybind 7 -c", GLFW.GLFW_KEY_U);
        createKeyBind("key.bind.ability1h", "/keybind 8 -c", GLFW.GLFW_KEY_G);
        createKeyBind("key.bind.ability1i", "/keybind 9 -c", GLFW.GLFW_KEY_B);
        createKeyBind("key.bind.ability1j", "/keybind 10 -c", GLFW.GLFW_KEY_Y);
        createKeyBind("key.bind.ability1k", "/keybind 11 -c", GLFW.GLFW_KEY_H);
        createKeyBind("key.bind.ability1l", "/keybind 12 -c", GLFW.GLFW_KEY_N);
        createKeyBind("key.bind.ability1m", "/keybind 13 -c", GLFW.GLFW_KEY_LEFT_ALT);
        createMouseBind("key.bind.ability1n", "/keybind 14 -c", GLFW.GLFW_MOUSE_BUTTON_4);
        createMouseBind("key.bind.ability1o", "/keybind 15 -c", GLFW.GLFW_MOUSE_BUTTON_5);
        createKeyBind("key.bind.ability1p", "/keybind 16 -c", -1);

        createMouseBind("key.bind.grenade1a", "/keybind 17 -c", GLFW.GLFW_MOUSE_BUTTON_6);
        createKeyBind("key.bind.grenade1b", "/keybind 18 -c",  GLFW.GLFW_KEY_O);
        createKeyBind("key.bind.grenade1c", "/keybind 19 -c", GLFW.GLFW_KEY_CAPS_LOCK);
        createKeyBind("key.bind.grenade1d", "/keybind 20 -c", GLFW.GLFW_KEY_F1);

        createKeyBind("key.bind.debug", entityPlayer -> {
            if (!passConfirm(entityPlayer)) return;
            JCMod.DEBUG = !JCMod.DEBUG;
            entityPlayer.sendMessage(new StringTextComponent("Debug mode" + (JCMod.DEBUG ? " enabled" : " disabled")), entityPlayer.getUUID());
            resetConfirm();
        },  GLFW.GLFW_KEY_F8);
        createKeyBind("key.bind.disable", entityPlayer -> {
            if (!passConfirm(entityPlayer)) return;
            JCMod.ENABLED = !JCMod.ENABLED;
            entityPlayer.sendMessage(new StringTextComponent("Binds " + (JCMod.ENABLED ? " enabled" : " disabled")), entityPlayer.getUUID());
            resetConfirm();
        },  GLFW.GLFW_KEY_F9);
        getCustomBind("key.bind.disable").skipDisabled();
    }

    private static boolean passConfirm(ClientPlayerEntity entityPlayer) {
        if (confirmTime == -1 || System.currentTimeMillis() > confirmTime) {
            confirmTime = System.currentTimeMillis() + 5000;
            entityPlayer.sendMessage(new StringTextComponent("Confirm action (double tap)"), entityPlayer.getUUID());
            return false;
        }
        return true;
    }

    private static void resetConfirm() {
        confirmTime = -1;
    }

    private static void createKeyBind(String techName, String command, int key) {
        createBind(techName, command, null, key, InputMappings.Type.KEYSYM);
    }

    private static void createMouseBind(String techName, String command, int key) {
        createBind(techName, command, null, key, InputMappings.Type.MOUSE);
    }

    private static void createKeyBind(String techName, Consumer<ClientPlayerEntity> consumer, int key) {
        createBind(techName, null, consumer, key, InputMappings.Type.KEYSYM);
    }

    private static void createBind(String techName, String command, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        CustomBind customBind = new CustomBind(techName, command, consumer, key, type);
        customBindMap.put(customBind.getTechName(), customBind);
        keybindsMap.add(customBind.getKeyBinding());
    }

    public static CustomBind getCustomBind(String techName) {
        return customBindMap.getOrDefault(techName, null);
    }

    public static void onInput() {
        Minecraft minecraft = JCMod.MINECRAFT;
        if (minecraft.screen != null) return;
        for (KeyBinding keyBinding : keybindsMap) {
            if (!keyBinding.isDown()) continue;
            CustomBind customBind = BindManager.getCustomBind(keyBinding.getName());
            if (customBind == null) continue;
            if (!JCMod.ENABLED && !customBind.isSkipDisabled()) return;
            customBind.handle(minecraft.player);
        }
    }
}
