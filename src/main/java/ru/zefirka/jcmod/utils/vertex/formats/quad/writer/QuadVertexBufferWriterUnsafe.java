package ru.zefirka.jcmod.utils.vertex.formats.quad.writer;

import org.lwjgl.system.MemoryUtil;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferWriterUnsafe;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;

public class QuadVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements QuadVertexSink {
    public QuadVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.QUADS);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        long i = this.writePointer;

        MemoryUtil.memPutFloat(i, x);
        MemoryUtil.memPutFloat(i + 4, y);
        MemoryUtil.memPutFloat(i + 8, z);
        MemoryUtil.memPutInt(i + 12, color);
        MemoryUtil.memPutFloat(i + 16, u);
        MemoryUtil.memPutFloat(i + 20, v);
        MemoryUtil.memPutInt(i + 24, overlay);
        MemoryUtil.memPutInt(i + 28, light);
        MemoryUtil.memPutInt(i + 32, normal);

        this.advance();
    }
}
