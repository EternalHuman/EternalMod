package ru.zefirka.jcmod.utils.vertex.formats.glyph.writer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.vertex.fallback.VertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.formats.glyph.GlyphVertexSink;

public class GlyphVertexWriterFallback extends VertexWriterFallback implements GlyphVertexSink {
    public GlyphVertexWriterFallback(IVertexBuilder consumer) {
        super(consumer);
    }

    @Override
    public void writeGlyph(float x, float y, float z, int color, float u, float v, int light) {
        IVertexBuilder consumer = this.consumer;
        consumer.vertex(x, y, z);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        consumer.uv(u, v);
        consumer.uv2(light);
        consumer.endVertex();
    }
}
