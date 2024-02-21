package ru.zefirka.jcmod.mixins.screens;

import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ru.zefirka.jcmod.JCMod;

@Mixin(IngameMenuScreen.class)
public class MixinIngameMenu extends Screen {
    protected MixinIngameMenu(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    /**
     * @author EternalHuman
     * @reason Rewrite ingame menu
     */
    @Overwrite
    public void createPauseMenu() {
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 - 16, 98, 20,
                new TranslationTextComponent("menu.vklink"), (button) ->
                minecraft.setScreen(new ConfirmOpenLinkScreen((confirm) -> {
                    if (confirm) Util.getPlatform().openUri(JCMod.VK_LINK);
                    minecraft.setScreen(this);
                }, JCMod.VK_LINK, true))));

        this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 - 16, 98, 20,
                new TranslationTextComponent("menu.dslink"), (button) ->
                minecraft.setScreen(new ConfirmOpenLinkScreen((confirm) -> {
                    if (confirm) Util.getPlatform().openUri(JCMod.DS_LINK);
                    minecraft.setScreen(this);
                }, JCMod.DS_LINK, true))));
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslationTextComponent("menu.returnToGame"), (p_213070_1_) -> {
            this.minecraft.setScreen((Screen)null);
            this.minecraft.mouseHandler.grabMouse();
        }));
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 204, 20, new TranslationTextComponent("gui.stats"), (p_213066_1_) -> {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 204, 20, new TranslationTextComponent("menu.options"), (p_213071_1_) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));
        Button button1 = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslationTextComponent("menu.returnToMenu"), (p_213067_1_) -> {
            boolean flag = this.minecraft.isLocalServer();
            boolean flag1 = this.minecraft.isConnectedToRealms();
            p_213067_1_.active = false;
            this.minecraft.level.disconnect();
            if (flag) {
                this.minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            if (flag) {
                this.minecraft.setScreen(new MainMenuScreen());
            } else if (flag1) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                realmsbridgescreen.switchToRealms(new MainMenuScreen());
            } else {
                this.minecraft.setScreen(new MultiplayerScreen(new MainMenuScreen()));
            }

        }));
        if (!this.minecraft.isLocalServer()) {
            button1.setMessage(new TranslationTextComponent("menu.disconnect"));
        }

    }

}
