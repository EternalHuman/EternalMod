package ru.zefirka.jcmod.utils.vertex.formats.quad.writer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.render.Norm3b;
import ru.zefirka.jcmod.utils.vertex.fallback.VertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;

public class QuadVertexWriterFallback extends VertexWriterFallback implements QuadVertexSink {
    public QuadVertexWriterFallback(IVertexBuilder consumer) {
        super(consumer);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        IVertexBuilder consumer = this.consumer;
        consumer.vertex(x, y, z);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        consumer.uv(u, v);
        consumer.overlayCoords(overlay);
        consumer.uv2(light);
        consumer.normal(Norm3b.unpackX(normal), Norm3b.unpackY(normal), Norm3b.unpackZ(normal));
        consumer.endVertex();
    }
}
