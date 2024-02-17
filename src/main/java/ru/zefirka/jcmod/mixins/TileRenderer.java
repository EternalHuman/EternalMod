package ru.zefirka.jcmod.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zefirka.jcmod.esp.ChestLocator;
import ru.zefirka.jcmod.utils.RenderUtils;

@Mixin(World.class)
public class TileRenderer {
    @Inject(method = "removeBlockEntity", at = @At("TAIL"))
    public void removeTile(BlockPos p_175713_1_, CallbackInfo ci) {
        if (ChestLocator.isEnabled()) {
            RenderUtils.removeChest(p_175713_1_);
        }
    }

    @Inject(method = "addBlockEntity", at = @At("TAIL"))
    public void addTile(TileEntity p_175700_1_, CallbackInfoReturnable<Boolean> cir) {
        if (ChestLocator.isEnabled()) {
            RenderUtils.addChest(p_175700_1_.getBlockPos());
        }
    }
}
