package ru.zefirka.jcmod.utils.vertex.type;

import net.minecraft.client.renderer.vertex.VertexFormat;
import ru.zefirka.jcmod.utils.vertex.BufferVertexFormat;
import ru.zefirka.jcmod.utils.vertex.VertexSink;

public interface VanillaVertexType<T extends VertexSink> extends BufferVertexType<T> {
    default BufferVertexFormat getBufferVertexFormat() {
        return BufferVertexFormat.from(this.getVertexFormat());
    }

    VertexFormat getVertexFormat();
}
