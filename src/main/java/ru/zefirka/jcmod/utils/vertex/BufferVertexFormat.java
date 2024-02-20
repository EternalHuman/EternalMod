package ru.zefirka.jcmod.utils.vertex;

import net.minecraft.client.renderer.vertex.VertexFormat;

public interface BufferVertexFormat {
    static BufferVertexFormat from(VertexFormat format) {
        return (BufferVertexFormat) format;
    }

    int getStride();
}
