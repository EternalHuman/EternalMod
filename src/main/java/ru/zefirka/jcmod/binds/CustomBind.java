package ru.zefirka.jcmod.binds;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import java.util.function.Consumer;

public class CustomBind {
    private final String command, techName;
    private final int key;
    private final InputMappings.Type type;
    private KeyBinding keyBinding;
    private final Consumer<ClientPlayerEntity> consumer;

    public CustomBind(String techName, String command, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        this.command = command;
        this.techName = techName;
        this.consumer = consumer;
        this.key = key;
        this.type = type;
        initKey();
    }

    public Consumer<ClientPlayerEntity> getConsumer() {
        return this.consumer;
    }

    public String getTechName() {
        return this.techName;
    }

    public String getCommand() {
        return this.command;
    }

    public KeyBinding getKey() {
        return this.keyBinding;
    }


    private void initKey() {
        this.keyBinding = new KeyBinding(this.techName, this.type, this.key, BindManager.CATEGORY);
        ClientRegistry.registerKeyBinding(this.keyBinding);
    }
}
