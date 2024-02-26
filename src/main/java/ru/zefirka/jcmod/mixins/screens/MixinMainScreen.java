package ru.zefirka.jcmod.mixins.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.updater.Updater;

@Mixin(MainMenuScreen.class)
public class MixinMainScreen extends Screen {
    private static final ServerData mainServer = new ServerData("Eternal", "31.184.215.54", true);

    static {
        mainServer.setResourcePackStatus(ServerData.ServerResourceMode.DISABLED);
    }

    protected MixinMainScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    /**
     * @author EternalHuman
     * @reason Rewrite menu screen
     */
    @Overwrite
    private void createNormalMenuOptions(int p_73969_1_, int p_73969_2_) {
        if (!Updater.REBOOT) {
            Button.ITooltip button$itooltip = Button.NO_TOOLTIP;

            (this.addButton(new Button(this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, 200, 20, new TranslationTextComponent("menu.select"), (p_213095_1_) -> {
                Screen screen = new MultiplayerScreen(this);
                this.minecraft.setScreen(screen);
            }, button$itooltip))).active = true;

            int diff = 24;
            int j = this.height / 4 + 48;

            addButton(new Button(this.width / 2 + 2, j + diff * 2, 98, 20,
                    new TranslationTextComponent("menu.site"), (button) ->
                    Util.getPlatform().openUri("https://discord.gg/BYXs8TBeEG")));

            addButton(new Button(this.width / 2 - 100, j, 200, 20,
                    new TranslationTextComponent("menu.play"), (button) ->
                    minecraft.setScreen(new ConnectingScreen(this, minecraft, mainServer))));
        }
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    protected void init(CallbackInfo ci) {
        if (Updater.REBOOT) {
            Minecraft.getInstance().forceSetScreen(new DirtMessageScreen(new TranslationTextComponent("menu.updated")));
        }
    }

}
