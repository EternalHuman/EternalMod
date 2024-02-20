package ru.zefirka.jcmod.utils.vertex.type;

import ru.zefirka.jcmod.utils.vertex.BufferVertexFormat;
import ru.zefirka.jcmod.utils.vertex.VertexSink;


public interface BufferVertexType<T extends VertexSink> extends VertexType<T> {
    BufferVertexFormat getBufferVertexFormat();
}
