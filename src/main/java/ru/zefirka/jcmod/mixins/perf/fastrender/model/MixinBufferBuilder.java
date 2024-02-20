package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.zefirka.jcmod.utils.vertex.BufferVertexFormat;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.VertexSink;
import ru.zefirka.jcmod.utils.vertex.buffer.VertexBufferView;
import ru.zefirka.jcmod.utils.vertex.type.BlittableVertexType;
import ru.zefirka.jcmod.utils.vertex.type.VertexType;

import java.nio.Buffer;
import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public class MixinBufferBuilder implements VertexBufferView, VertexDrain {
    @Shadow
    private int nextElementByte;

    @Shadow
    private ByteBuffer buffer;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private static int roundUp(int amount) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private VertexFormat format;

    @Shadow
    private int vertices;

    @Redirect(method = "popNextBuffer", at = @At(value = "INVOKE", target = "Ljava/nio/Buffer;limit(I)Ljava/nio/Buffer;"))
    public Buffer debugGetNextBuffer(Buffer buffer, int newLimit) {
        ensureBufferCapacity(newLimit);
        buffer = (Buffer) this.buffer;
        buffer.limit(newLimit);
        return buffer;
    }

    @Override
    public boolean ensureBufferCapacity(int bytes) {
        if(format == null)
            return false;

        // Ensure that there is always space for 1 more vertex; see BufferBuilder.next()
        bytes += format.getVertexSize();

        if (this.nextElementByte + bytes <= this.buffer.capacity()) {
            return false;
        }

        int newSize = this.buffer.capacity() + roundUp(bytes);

        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", this.buffer.capacity(), newSize);

        this.buffer.position(0);

        ByteBuffer byteBuffer = GLAllocation.createByteBuffer(newSize);
        byteBuffer.put(this.buffer);
        byteBuffer.rewind();

        this.buffer = byteBuffer;

        return true;
    }

    @Override
    public ByteBuffer getDirectBuffer() {
        return this.buffer;
    }

    @Override
    public int getWriterPosition() {
        return this.nextElementByte;
    }

    @Override
    public BufferVertexFormat getVertexFormat() {
        return BufferVertexFormat.from(this.format);
    }

    @Override
    public void flush(int vertexCount, BufferVertexFormat format) {
        if (BufferVertexFormat.from(this.format) != format) {
            throw new IllegalStateException("Mis-matched vertex format (expected: [" + format + "], currently using: [" + this.format + "])");
        }

        this.vertices += vertexCount;
        this.nextElementByte += vertexCount * format.getStride();
    }

    @Override
    public <T extends VertexSink> T createSink(VertexType<T> factory) {
        BlittableVertexType<T> blittable = factory.asBlittable();

        if (blittable != null && blittable.getBufferVertexFormat() == this.getVertexFormat())  {
            return blittable.createBufferWriter(this, true);
        }

        return factory.createFallbackWriter((IVertexBuilder) this);
    }
}
