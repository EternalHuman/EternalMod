package ru.zefirka.jcmod.utils.vertex.formats.glyph;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.formats.glyph.writer.GlyphVertexBufferWriterNio;
import ru.zefirka.jcmod.utils.vertex.formats.glyph.writer.GlyphVertexBufferWriterUnsafe;
import ru.zefirka.jcmod.utils.vertex.formats.glyph.writer.GlyphVertexWriterFallback;
import ru.zefirka.jcmod.utils.vertex.type.BlittableVertexType;
import ru.zefirka.jcmod.utils.vertex.type.VanillaVertexType;

public class GlyphVertexType implements VanillaVertexType<GlyphVertexSink>, BlittableVertexType<GlyphVertexSink> {
    @Override
    public GlyphVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new GlyphVertexBufferWriterUnsafe(buffer) : new GlyphVertexBufferWriterNio(buffer);
    }

    @Override
    public GlyphVertexSink createFallbackWriter(IVertexBuilder consumer) {
        return new GlyphVertexWriterFallback(consumer);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return GlyphVertexSink.VERTEX_FORMAT;
    }

    @Override
    public BlittableVertexType<GlyphVertexSink> asBlittable() {
        return this;
    }
}
