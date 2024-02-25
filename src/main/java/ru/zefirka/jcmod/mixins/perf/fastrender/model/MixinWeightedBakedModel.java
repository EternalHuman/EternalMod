package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import java.util.List;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zefirka.jcmod.utils.baked.DynamicWeightedBakedModel;

@Mixin(WeightedBakedModel.Builder.class)
public class MixinWeightedBakedModel {
    @Shadow
    @Final
    private List<WeightedBakedModel.WeightedModel> list;

    @Inject(method = "build", at = @At(value = "NEW", target = "(Ljava/util/List;)Lnet/minecraft/client/renderer/model/WeightedBakedModel;", shift = At.Shift.BEFORE), cancellable = true)
    private void injectBuild(CallbackInfoReturnable<IBakedModel> cir) {
        cir.setReturnValue(new DynamicWeightedBakedModel(this.list));
    }
}