package ru.zefirka.jcmod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.binds.BindManager;
import ru.zefirka.jcmod.binds.CustomBind;
import ru.zefirka.jcmod.JCMod;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyEventHandler
{
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        onInput();
    }

    @SubscribeEvent
    public static void onMousePress(InputEvent.MouseInputEvent event) {
        onInput();
    }

    private static void onInput() {
        Minecraft minecraft = JCMod.MINECRAFT;
        if (minecraft.screen != null) return;
        if (!JCMod.ENABLED) return;
        ClientPlayerEntity clientPlayerEntity = minecraft.player;
        for (KeyBinding keyBinding : BindManager.keybindsMap) {
            if (keyBinding.isDown()) {
                CustomBind customBind = BindManager.getCustomBind(keyBinding.getName());
                System.out.println(customBind);
                if (customBind != null) {
                    if (customBind.getConsumer() != null) {
                        customBind.getConsumer().accept(clientPlayerEntity);
                        return;
                    }
                    /*ServerData serverData = minecraft.getCurrentServer();
                    if (serverData == null) return; */
                    debug("M #4 SD NOT NULL");
                    clientPlayerEntity.chat(customBind.getCommand());
                }
            }
        }
    }

    public static void debug(String text) {
        if (!JCMod.DEBUG) return;
        JCMod.MINECRAFT.player.sendMessage(new StringTextComponent(text), Minecraft.getInstance().player.getUUID());
    }
}