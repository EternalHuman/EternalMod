package ru.zefirka.jcmod.utils.vertex.type;

import ru.zefirka.jcmod.utils.vertex.VertexSink;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;

public interface BlittableVertexType<T extends VertexSink> extends BufferVertexType<T> {
    /**
     * Creates a {@link VertexSink} which writes into a {@link VertexBufferView}. This allows for specialization
     * when the memory storage is known.
     *
     * @param buffer The backing vertex buffer
     * @param direct True if direct memory access is allowed, otherwise false
     */
    T createBufferWriter(VertexBufferView buffer, boolean direct);
}
