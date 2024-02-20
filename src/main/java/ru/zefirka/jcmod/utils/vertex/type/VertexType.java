package ru.zefirka.jcmod.utils.vertex.type;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import ru.zefirka.jcmod.utils.vertex.VertexSink;

/**
 * Provides factories which create a {@link VertexSink} for the given vertex format.
 *
 * @param <T> The {@link VertexSink} type this factory produces
 */
public interface VertexType<T extends VertexSink> {

    T createFallbackWriter(IVertexBuilder consumer);

    default BlittableVertexType<T> asBlittable() {
        return null;
    }
}
