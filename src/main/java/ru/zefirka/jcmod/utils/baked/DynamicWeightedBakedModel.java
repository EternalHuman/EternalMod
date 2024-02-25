package ru.zefirka.jcmod.utils.baked;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DynamicWeightedBakedModel extends DynamicBakedModel {
    private final int totalWeight;
    private final List<WeightedBakedModel.WeightedModel> models;

    public DynamicWeightedBakedModel(List<WeightedBakedModel.WeightedModel> models) {
        super(models.get(0).model);

        this.models = models;
        this.totalWeight = WeightedRandom.getTotalWeight(models);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random random) {
        WeightedBakedModel.WeightedModel result = this.getWeightedModel(random);

        if (result != null) {
            return result.model
                    .getQuads(state, side, random);
        }

        return Collections.emptyList();
    }

    private WeightedBakedModel.WeightedModel getWeightedModel(Random random) {
        return getValueForWeight(this.models, Math.abs((int) random.nextLong()) % this.totalWeight);
    }

    private static <T extends WeightedBakedModel.WeightedModel> T getValueForWeight(List<T> pool, int totalWeight) {
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
