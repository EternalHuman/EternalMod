package ru.zefirka.jcmod.utils.vertex.formats.quad;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.math.Matrix4fExtended;
import ru.zefirka.jcmod.utils.math.MatrixUtil;
import ru.zefirka.jcmod.utils.vertex.VertexSink;

public interface QuadVertexSink extends VertexSink {
    VertexFormat VERTEX_FORMAT = DefaultVertexFormats.NEW_ENTITY;

    /**
     * Writes a quad vertex to this sink.
     *
     * @param x The x-position of the vertex
     * @param y The y-position of the vertex
     * @param z The z-position of the vertex
     * @param color The ABGR-packed color of the vertex
     * @param u The u-texture of the vertex
     * @param v The y-texture of the vertex
     * @param light The packed light-map coordinates of the vertex
     * @param overlay The packed overlay-map coordinates of the vertex
     * @param normal The 3-byte packed normal vector of the vertex
     */
    void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal);

    /**
     * Writes a quad vertex to the sink, transformed by the given matrices.
     *
     * @param matrices The matrices to transform the vertex's position and normal vectors by
     */
    default void writeQuad(MatrixStack.Entry matrices, float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
        Matrix4fExtended modelMatrix = MatrixUtil.getExtendedMatrix(matrices.pose());

        float x2 = modelMatrix.transformVecX(x, y, z);
        float y2 = modelMatrix.transformVecY(x, y, z);
        float z2 = modelMatrix.transformVecZ(x, y, z);

        int norm = MatrixUtil.transformPackedNormal(normal, matrices.normal());

        this.writeQuad(x2, y2, z2, color, u, v, light, overlay, norm);
    }
}
