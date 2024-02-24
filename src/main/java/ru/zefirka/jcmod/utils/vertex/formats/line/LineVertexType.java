package ru.zefirka.jcmod.utils.vertex.formats.line;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.formats.line.writer.LineVertexBufferWriterNio;
import ru.zefirka.jcmod.utils.vertex.formats.line.writer.LineVertexBufferWriterUnsafe;
import ru.zefirka.jcmod.utils.vertex.formats.line.writer.LineVertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.type.BlittableVertexType;
import ru.zefirka.jcmod.utils.vertex.type.VanillaVertexType;

public class LineVertexType implements VanillaVertexType<LineVertexSink>, BlittableVertexType<LineVertexSink> {
    @Override
    public LineVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new LineVertexBufferWriterUnsafe(buffer) : new LineVertexBufferWriterNio(buffer);
    }

    @Override
    public LineVertexSink createFallbackWriter(IVertexBuilder consumer) {
        return new LineVertexWriterFallback(consumer);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return LineVertexSink.VERTEX_FORMAT;
    }

    @Override
    public BlittableVertexType<LineVertexSink> asBlittable() {
        return this;
    }
}
