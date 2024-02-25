package ru.zefirka.jcmod.utils.baked;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2LongLinkedOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.tuple.Pair;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class DynamicMultipartBakedModel extends DynamicBakedModel {
    // Maximum number of components for the block model
    // This is one minus the number of bits in a 64-bit integer, as we use the most significant
    // bit as a sentinel.
    public static final int MAX_COMPONENT_COUNT = 63;

    // Maximum number of entries that can exist in the cache at any given time. If the cache becomes full,
    // the least recently accessed entries will be evicted first.
    private static final int MAX_CACHE_SIZE = 128;

    // Sentinel value which indicates that the cache is missing an entry
    private static final long EMPTY_CACHE_VALUE = 1L << MAX_COMPONENT_COUNT;

    // Avoid Pair/List de-referencing for performance by splitting the values into two arrays
    private final Predicate<BlockState>[] predicates;
    private final IBakedModel[] models;

    private final Reference2LongLinkedOpenHashMap<BlockState> cache;

    @SuppressWarnings("unchecked")
    public DynamicMultipartBakedModel(List<Pair<Predicate<BlockState>, IBakedModel>> components) {
        super(components.get(0).getRight());

        if (components.size() > MAX_COMPONENT_COUNT) {
            throw new IllegalArgumentException("Component count must be <" + MAX_COMPONENT_COUNT);
        }

        this.cache = new Reference2LongLinkedOpenHashMap<>();
        this.cache.defaultReturnValue(EMPTY_CACHE_VALUE);

        this.predicates = new Predicate[components.size()];
        this.models = new IBakedModel[components.size()];

        for (int i = 0; i < components.size(); i++) {
            Pair<Predicate<BlockState>, IBakedModel> pair = components.get(i);

            this.predicates[i] = pair.getLeft();
            this.models[i] = pair.getRight();
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        if (state == null) {
            return Collections.emptyList();
        }

        final long bits = this.getBitField(state);

        List<BakedQuad> list = Lists.newArrayList();
        long seed = rand.nextLong();

        long cur = bits;
        int idx = 0;

        // Iterate over set bits of the field, starting at the zeroth bit
        while ((idx += Long.numberOfTrailingZeros(cur)) < MAX_COMPONENT_COUNT) {
            IBakedModel model = this.models[idx];
            list.addAll(model.getQuads(state, side, new Random(seed)));

            idx += 1;
            cur = bits >>> idx;
        }

        return list;
    }

    private long getBitField(BlockState state) {
        long bits;

        // Shared access must be synchronized
        // The vanilla multi-part model rarely throws a concurrent modification exception because of this,
        // and the problem seems significantly agitated with Sodium.
        synchronized (this.cache) {
            bits = this.cache.getAndMoveToFirst(state);

            if (bits == EMPTY_CACHE_VALUE) {
                // Evict old entries if the cache is too large, leaving (limit - 1) entries in the cache
                while (this.cache.size() >= MAX_CACHE_SIZE) {
                    this.cache.removeLastLong();
                }

                this.cache.put(state, bits = this.createComponentBitField(state));
            }
        }

        return bits;
    }

    private long createComponentBitField(BlockState state) {
        long bits = 0L;

        for (int i = 0; i < this.predicates.length; ++i) {
            Predicate<BlockState> predicate = this.predicates[i];
            bits |= ((predicate.test(state) ? 1L : 0L) << i);
        }

        return bits;
    }
}
