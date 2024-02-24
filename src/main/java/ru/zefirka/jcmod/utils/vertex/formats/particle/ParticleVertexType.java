package ru.zefirka.jcmod.utils.vertex.formats.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.formats.particle.writer.ParticleVertexBufferWriterNio;
import ru.zefirka.jcmod.utils.vertex.formats.particle.writer.ParticleVertexBufferWriterUnsafe;
import ru.zefirka.jcmod.utils.vertex.formats.particle.writer.ParticleVertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.type.BlittableVertexType;
import ru.zefirka.jcmod.utils.vertex.type.VanillaVertexType;

public class ParticleVertexType implements VanillaVertexType<ParticleVertexSink>, BlittableVertexType<ParticleVertexSink> {
    @Override
    public ParticleVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new ParticleVertexBufferWriterUnsafe(buffer) : new ParticleVertexBufferWriterNio(buffer);
    }

    @Override
    public ParticleVertexSink createFallbackWriter(IVertexBuilder consumer) {
        return new ParticleVertexWriterFallback(consumer);
    }

    @Override
    public BlittableVertexType<ParticleVertexSink> asBlittable() {
        return this;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return ParticleVertexSink.VERTEX_FORMAT;
    }
}
