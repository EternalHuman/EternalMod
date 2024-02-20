package ru.zefirka.jcmod.utils.vertex;

import ru.zefirka.jcmod.utils.vertex.formats.glyph.GlyphVertexSink;
import ru.zefirka.jcmod.utils.vertex.formats.glyph.GlyphVertexType;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexType;
import ru.zefirka.jcmod.utils.vertex.type.VanillaVertexType;

public class VanillaVertexTypes {
    public static final VanillaVertexType<QuadVertexSink> QUADS = new QuadVertexType();
    public static final VanillaVertexType<GlyphVertexSink> GLYPHS = new GlyphVertexType();
}
