package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zefirka.jcmod.utils.baked.DynamicMultipartBakedModel;
import java.util.*;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.Builder.class)
public class MixinMultipartBakedModel {
    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, IBakedModel>> selectors;

    @Inject(method = "build", at = @At("HEAD"), cancellable = true)
    private void injectBuild(CallbackInfoReturnable<IBakedModel> cir) {
        if (this.selectors.size() <= DynamicMultipartBakedModel.MAX_COMPONENT_COUNT) {
            cir.setReturnValue(new DynamicMultipartBakedModel(this.selectors));
        }
    }
}
