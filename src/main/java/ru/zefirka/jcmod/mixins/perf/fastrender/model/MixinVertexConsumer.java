package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.VertexSink;
import ru.zefirka.jcmod.utils.vertex.type.VertexType;

@Mixin(IVertexBuilder.class)
public interface MixinVertexConsumer extends VertexDrain {
    @Override
    default <T extends VertexSink> T createSink(VertexType<T> factory) {
        return factory.createFallbackWriter((IVertexBuilder) this);
    }
}
