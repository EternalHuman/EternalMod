package ru.zefirka.jcmod.utils.vertex.formats.particle;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.vertex.VertexSink;

public interface ParticleVertexSink extends VertexSink {
    VertexFormat VERTEX_FORMAT = DefaultVertexFormats.PARTICLE;

    /**
     * @param x The x-position of the vertex
     * @param y The y-position of the vertex
     * @param z The z-position of the vertex
     * @param u The u-texture of the vertex
     * @param v The v-texture of the vertex
     * @param color The ABGR-packed color of the vertex
     * @param light The packed light map texture coordinates of the vertex
     */
    void writeParticle(float x, float y, float z, float u, float v, int color, int light);
}
