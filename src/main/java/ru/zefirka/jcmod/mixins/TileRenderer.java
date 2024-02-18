package ru.zefirka.jcmod.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.zefirka.jcmod.esp.ChestLocator;
import ru.zefirka.jcmod.utils.RenderUtils;

@Mixin(World.class)
public class TileRenderer {
    @Shadow
    public java.util.Set<TileEntity> blockEntitiesToUnload;

    @Inject(method = "tickBlockEntities", at = @At("HEAD"))
    public void removeTile(CallbackInfo ci) {
        if (ChestLocator.isEnabled()) {
            this.blockEntitiesToUnload.forEach(tileEntity -> {
                if (tileEntity.getType() != TileEntityType.CHEST) return;
                RenderUtils.removeChest(tileEntity.getBlockPos());
            });
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void removeTile(BlockPos p_175713_1_, CallbackInfo ci, TileEntity tileentity) {
        if (ChestLocator.isEnabled() && tileentity.getType() == TileEntityType.CHEST) {
            RenderUtils.removeChest(tileentity.getBlockPos());
        }
    }

    @Inject(method = "addBlockEntity", at = @At("TAIL"))
    public void addTile(TileEntity p_175700_1_, CallbackInfoReturnable<Boolean> cir) {
        if (ChestLocator.isEnabled() && p_175700_1_.getType() == TileEntityType.CHEST) {
            RenderUtils.addChest(p_175700_1_.getBlockPos());
        }
    }
}
