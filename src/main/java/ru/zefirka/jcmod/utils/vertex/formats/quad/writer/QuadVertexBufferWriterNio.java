package ru.zefirka.jcmod.utils.vertex.formats.quad.writer;

import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferWriterNio;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;

import java.nio.ByteBuffer;

public class QuadVertexBufferWriterNio extends VertexBufferWriterNio implements QuadVertexSink {
    public QuadVertexBufferWriterNio(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.QUADS);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        int i = this.writeOffset;

        ByteBuffer buf = this.byteBuffer;
        buf.putFloat(i, x);
        buf.putFloat(i + 4, y);
        buf.putFloat(i + 8, z);
        buf.putInt(i + 12, color);
        buf.putFloat(i + 16, u);
        buf.putFloat(i + 20, v);
        buf.putInt(i + 24, overlay);
        buf.putInt(i + 28, light);
        buf.putInt(i + 32, normal);

        this.advance();
    }
}
