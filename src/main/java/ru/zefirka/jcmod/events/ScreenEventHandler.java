package ru.zefirka.jcmod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.zefirka.jcmod.JCMod;
import ru.zefirka.jcmod.updater.Updater;
import ru.zefirka.jcmod.utils.ResourcePack;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = JCMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenEventHandler {

    @SubscribeEvent
    public static void onEvent(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        Minecraft minecraft = JCMod.MINECRAFT;
        Iterator<Widget> widgetIterator = event.getWidgetList().iterator();
        if (screen instanceof MainMenuScreen) {
            ResourcePack.updateRP(minecraft);
            MainMenuScreen mainMenuScreen = (MainMenuScreen) screen;
            int diff = 24;
            int j = mainMenuScreen.height / 4 + 48;
            while (widgetIterator.hasNext()) {
                Widget widget = widgetIterator.next();
                if (!(widget.getMessage() instanceof TranslationTextComponent)) return;
                TranslationTextComponent translationTextComponent = (TranslationTextComponent) widget.getMessage();
                String key = translationTextComponent.getKey();
                if (Updater.REBOOT) {
                    if (key.equals("menu.multiplayer")) {
                        widget.setMessage(new TranslationTextComponent("menu.updated"));
                    } else {
                        widget.active = false;
                        widget.visible = false;
                    }
                } else {
                    if (key.equals("menu.singleplayer") ||
                            key.equals("menu.online")) {
                        widget.visible = false;
                        widget.active = false;
                    } else if (key.equals("menu.multiplayer")) {
                        widget.active = true;
                        widget.setMessage(new TranslationTextComponent("menu.select"));
                    }
                }
                System.out.println(translationTextComponent.getKey());
            }

            event.addWidget(new Button(mainMenuScreen.width / 2 + 2, j + diff * 2, 98, 20,
                    new TranslationTextComponent("menu.site"), (button) -> {
                Util.getPlatform().openUri("https://discord.gg/BYXs8TBeEG");
            }));
            event.addWidget(new Button(mainMenuScreen.width / 2 - 100, j, 200, 20,
                    new TranslationTextComponent("menu.play"), (button) -> {
                minecraft.setScreen(new ConnectingScreen(mainMenuScreen, minecraft,
                        "31.184.215.54", 25565));
            }));
        } else if (screen instanceof IngameMenuScreen) {
            IngameMenuScreen ingameMenuScreen = (IngameMenuScreen) screen;
            while (widgetIterator.hasNext()) {
                Widget widget = widgetIterator.next();
                if (!(widget.getMessage() instanceof TranslationTextComponent)) return;
                TranslationTextComponent translationTextComponent = (TranslationTextComponent) widget.getMessage();
                String key = translationTextComponent.getKey();
                if (key.equals("menu.shareToLan") || key.equals("gui.advancements") ||
                        key.equals("menu.sendFeedback") || key.equals("menu.reportBugs")) {
                    widget.visible = false;
                    widget.active = false;
                } else if (key.equals("menu.options")) {
                    widget.setWidth(204);
                } else if (key.equals("gui.stats")) {
                    widget.setWidth(204);
                    widget.x = ingameMenuScreen.width / 2 - 102;
                }
            }
            event.addWidget(new Button(ingameMenuScreen.width / 2 - 102, ingameMenuScreen.height / 4 + 72 + -16, 98, 20,
                    new TranslationTextComponent("menu.vklink"), (button) -> {
                minecraft.setScreen(new ConfirmOpenLinkScreen((confirm) -> {
                    if (confirm) Util.getPlatform().openUri(JCMod.VK_LINK);
                    minecraft.setScreen(ingameMenuScreen);
                }, JCMod.VK_LINK, true));
            }));
            event.addWidget(new Button(ingameMenuScreen.width / 2 + 4, ingameMenuScreen.height / 4 + 72 + -16, 98, 20,
                    new TranslationTextComponent("menu.dslink"), (button) -> {
                minecraft.setScreen(new ConfirmOpenLinkScreen((confirm) -> {
                    if (confirm) Util.getPlatform().openUri(JCMod.DS_LINK);
                    minecraft.setScreen(ingameMenuScreen);
                }, JCMod.DS_LINK, true));
            }));
        } else if (screen instanceof InventoryScreen) {
            while (widgetIterator.hasNext()) {
                Widget widget = widgetIterator.next();
                if (widget instanceof ImageButton) {
                    widget.visible = false;
                    widget.active = false;
                }
            }
        }
    }
}
