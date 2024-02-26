package ru.zefirka.jcmod.mixins.unrender;

import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.culling.Cullable;

import java.util.Random;

@Mixin(FurnaceBlock.class)
public class FurnaceAnimate {
    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void tickEntity(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_, CallbackInfo ci) {
        TileEntity tileEntity = p_180655_2_.getBlockEntity(p_180655_3_);
        if (tileEntity != null && ((Cullable) tileEntity).isCulled()) {
            ci.cancel();
        }
    }
}
