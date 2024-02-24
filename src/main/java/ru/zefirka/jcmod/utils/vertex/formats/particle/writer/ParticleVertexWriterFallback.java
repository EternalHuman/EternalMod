package ru.zefirka.jcmod.utils.vertex.formats.particle.writer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.vertex.fallback.VertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.formats.particle.ParticleVertexSink;

public class ParticleVertexWriterFallback extends VertexWriterFallback implements ParticleVertexSink {
    public ParticleVertexWriterFallback(IVertexBuilder consumer) {
        super(consumer);
    }

    @Override
    public void writeParticle(float x, float y, float z, float u, float v, int color, int light) {
        IVertexBuilder consumer = this.consumer;
        consumer.vertex(x, y, z);
        consumer.uv(u, v);
        consumer.color(ColorABGR.unpackRed(color), ColorABGR.unpackGreen(color), ColorABGR.unpackBlue(color), ColorABGR.unpackAlpha(color));
        consumer.uv2(light);
        consumer.endVertex();
    }
}
