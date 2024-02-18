package ru.zefirka.jcmod.mixins;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.culling.DebugStats;

import java.util.Random;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    public void tickEntity(Entity entity, CallbackInfo info) {
        DebugStats.tickedEntities++;
    }
}
