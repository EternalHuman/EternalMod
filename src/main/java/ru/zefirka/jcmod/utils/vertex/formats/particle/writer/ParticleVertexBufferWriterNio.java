package ru.zefirka.jcmod.utils.vertex.formats.particle.writer;

import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferWriterNio;
import ru.zefirka.jcmod.utils.vertex.formats.particle.ParticleVertexSink;

import java.nio.ByteBuffer;

public class ParticleVertexBufferWriterNio extends VertexBufferWriterNio implements ParticleVertexSink {
    public ParticleVertexBufferWriterNio(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.PARTICLES);
    }

    @Override
    public void writeParticle(float x, float y, float z, float u, float v, int color, int light) {
        int i = this.writeOffset;

        ByteBuffer buffer = this.byteBuffer;
        buffer.putFloat(i, x);
        buffer.putFloat(i + 4, y);
        buffer.putFloat(i + 8, z);
        buffer.putFloat(i + 12, u);
        buffer.putFloat(i + 16, v);
        buffer.putInt(i + 20, color);
        buffer.putInt(i + 24, light);

        this.advance();
    }
}