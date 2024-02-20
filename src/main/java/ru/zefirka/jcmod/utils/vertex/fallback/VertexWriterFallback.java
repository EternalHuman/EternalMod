package ru.zefirka.jcmod.utils.vertex.fallback;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import ru.zefirka.jcmod.utils.vertex.VertexSink;


public abstract class VertexWriterFallback implements VertexSink {
    protected final IVertexBuilder consumer;

    protected VertexWriterFallback(IVertexBuilder consumer) {
        this.consumer = consumer;
    }

    @Override
    public void ensureCapacity(int count) {
        // NO-OP
    }

    @Override
    public void flush() {
        // NO-OP
    }
}
