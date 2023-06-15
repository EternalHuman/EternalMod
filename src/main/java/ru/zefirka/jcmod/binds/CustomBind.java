package ru.zefirka.jcmod.binds;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.function.Consumer;

public class CustomBind {
    private String command, techName;
    private int key;
    private long lastPressTime;
    private InputMappings.Type type;
    private KeyBinding keyBinding;

    private Consumer<ClientPlayerEntity> consumer;

    public CustomBind(String techName, String command, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        this.command = command;
        this.techName = techName;
        this.consumer = consumer;
        this.key = key;
        this.type = type;
        initKey();
    }

    public CustomBind(String techName, String command, int key, InputMappings.Type type) {
        this(techName, command, null, key, type);
    }

    public CustomBind(String techName, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        this(techName, null, consumer, key, type);
    }

    public void setLastPressTime(long lastPressTime) {
        this.lastPressTime = lastPressTime;
    }

    public long getLastPressTime() {
        return lastPressTime;
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
