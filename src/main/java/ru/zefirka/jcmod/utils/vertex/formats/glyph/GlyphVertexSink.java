package ru.zefirka.jcmod.utils.vertex.formats.glyph;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import ru.zefirka.jcmod.utils.math.Matrix4fExtended;
import ru.zefirka.jcmod.utils.math.MatrixUtil;
import ru.zefirka.jcmod.utils.vertex.VertexSink;

public interface GlyphVertexSink extends VertexSink {
    VertexFormat VERTEX_FORMAT = DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP;

    /**
     * Writes a glyph vertex to the sink.
     *
     * @param matrix The transformation matrix to apply to the vertex's position
     * @see GlyphVertexSink#writeGlyph(float, float, float, int, float, float, int)
     */
    default void writeGlyph(Matrix4f matrix, float x, float y, float z, int color, float u, float v, int light) {
        Matrix4fExtended matrixExt = MatrixUtil.getExtendedMatrix(matrix);

        float x2 = matrixExt.transformVecX(x, y, z);
        float y2 = matrixExt.transformVecY(x, y, z);
        float z2 = matrixExt.transformVecZ(x, y, z);

        this.writeGlyph(x2, y2, z2, color, u, v, light);
    }

    /**
     * Writes a glyph vertex to the sink.
     *
     * @param x The x-position of the vertex
     * @param y The y-position of the vertex
     * @param z The z-position of the vertex
     * @param color The ABGR-packed color of the vertex
     * @param u The u-texture of the vertex
     * @param v The v-texture of the vertex
     * @param light The packed light map texture coordinates of the vertex
     */
    void writeGlyph(float x, float y, float z, int color, float u, float v, int light);
}
