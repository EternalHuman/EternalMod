package ru.zefirka.jcmod.binds;

import lombok.Getter;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import ru.zefirka.jcmod.JCMod;

import java.util.function.Consumer;

public class CustomBind {
    @Getter
    private final String command, techName;
    private final int key;
    private final InputMappings.Type type;
    @Getter
    private KeyBinding keyBinding;
    @Getter
    private final Consumer<ClientPlayerEntity> consumer;

    public CustomBind(String techName, String command, Consumer<ClientPlayerEntity> consumer, int key, InputMappings.Type type) {
        this.command = command;
        this.techName = techName;
        this.consumer = consumer;
        this.key = key;
        this.type = type;
        initKey();
    }

    protected void handle(ClientPlayerEntity clientPlayerEntity) {
        JCMod.debug("M #4 SD NOT NULL");
        if (this.consumer != null) {
            this.consumer.accept(clientPlayerEntity);
        } else {
            clientPlayerEntity.chat(this.command);
        }
    }

    private void initKey() {
        this.keyBinding = new KeyBinding(this.techName, this.type, this.key, BindManager.CATEGORY);
        ClientRegistry.registerKeyBinding(this.keyBinding);
    }
}
