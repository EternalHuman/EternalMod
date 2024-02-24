package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.class)
public class MixinMultipartBakedModel {
    private final Map<BlockState, IBakedModel[]> stateCacheFast = new Reference2ReferenceOpenHashMap<>();
    private final StampedLock lock = new StampedLock();

    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, IBakedModel>> selectors;

    /**
     * @author EternalHuman
     * @reason Avoid expensive allocations and replace bitfield indirection
     */
    @Overwrite(remap = false)
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random, IModelData modelData) {
        if (state == null) {
            return Collections.emptyList();
        }

        IBakedModel[] models;

        long readStamp = this.lock.readLock();
        try {
            models = this.stateCacheFast.get(state);
        } finally {
            this.lock.unlockRead(readStamp);
        }

        if (models == null) {
            long writeStamp = this.lock.writeLock();
            try {
                List<IBakedModel> modelList = new ArrayList<>(this.selectors.size());

                for (Pair<Predicate<BlockState>, IBakedModel> pair : this.selectors) {
                    if (pair.getLeft().test(state)) {
                        modelList.add(pair.getRight());
                    }
                }

                models = modelList.toArray(new IBakedModel[modelList.size()]);
                this.stateCacheFast.put(state, models);
            } finally {
                this.lock.unlockWrite(writeStamp);
            }
        }

        List<BakedQuad> quads = new ArrayList<>();
        long seed = random.nextLong();

        for (IBakedModel model : models) {
            random.setSeed(seed);
            quads.addAll(model.getQuads(state, face, random, modelData));
        }

        return quads;
    }

}
