package ru.zefirka.jcmod.utils.vertex.formats.glyph.writer;

import org.lwjgl.system.MemoryUtil;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferWriterUnsafe;
import ru.zefirka.jcmod.utils.vertex.formats.glyph.GlyphVertexSink;

public class GlyphVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements GlyphVertexSink {
    public GlyphVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexTypes.GLYPHS);
    }

    @Override
    public void writeGlyph(float x, float y, float z, int color, float u, float v, int light) {
        long i = this.writePointer;

        MemoryUtil.memPutFloat(i, x);
        MemoryUtil.memPutFloat(i + 4, y);
        MemoryUtil.memPutFloat(i + 8, z);
        MemoryUtil.memPutInt(i + 12, color);
        MemoryUtil.memPutFloat(i + 16, u);
        MemoryUtil.memPutFloat(i + 20, v);
        MemoryUtil.memPutInt(i + 24, light);

        this.advance();

    }
}
