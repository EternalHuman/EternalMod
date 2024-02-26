package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import javax.annotation.Nullable;

@Mixin(WeightedBakedModel.class)
public class MixinWeightedBakedModel {
    @Shadow
    @Final
    private List<WeightedBakedModel.WeightedModel> list;

    @Shadow
    @Final
    private int totalWeight;

    /**
     * @author EternalHuman
     * @reason Avoid excessive object allocations
     */
    @Overwrite(remap = false)
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random, IModelData modelData) {
        WeightedBakedModel.WeightedModel entry = getAt(this.list, Math.abs((int) random.nextLong()) % this.totalWeight);

        if (entry != null) {
            return entry.model.getQuads(state, face, random, modelData);
        }

        return Collections.emptyList();
    }

    private static <T extends WeightedBakedModel.WeightedModel> T getAt(List<T> pool, int totalWeight) {
        int i = 0;
        int len = pool.size();

        T weighted;

        do {
            if (i >= len) {
                return null;
            }

            weighted = pool.get(i++);
            totalWeight -= weighted.weight;
        } while (totalWeight >= 0);

        return weighted;
    }
}