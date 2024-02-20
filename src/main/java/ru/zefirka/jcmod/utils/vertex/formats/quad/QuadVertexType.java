package ru.zefirka.jcmod.utils.vertex.formats.quad;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.formats.quad.writer.QuadVertexBufferWriterNio;
import ru.zefirka.jcmod.utils.vertex.formats.quad.writer.QuadVertexBufferWriterUnsafe;
import ru.zefirka.jcmod.utils.vertex.formats.quad.writer.QuadVertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.type.BlittableVertexType;
import ru.zefirka.jcmod.utils.vertex.type.VanillaVertexType;

public class QuadVertexType implements VanillaVertexType<QuadVertexSink>, BlittableVertexType<QuadVertexSink> {
    @Override
    public QuadVertexSink createFallbackWriter(IVertexBuilder consumer) {
        return new QuadVertexWriterFallback(consumer);
    }

    @Override
    public QuadVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new QuadVertexBufferWriterUnsafe(buffer) : new QuadVertexBufferWriterNio(buffer);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return QuadVertexSink.VERTEX_FORMAT;
    }

    @Override
    public BlittableVertexType<QuadVertexSink> asBlittable() {
        return this;
    }
}
