package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.SpriteAwareVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.VertexSink;
import ru.zefirka.jcmod.utils.vertex.transformers.SpriteTexturedVertexTransformer;
import ru.zefirka.jcmod.utils.vertex.type.VertexType;

@Mixin(SpriteAwareVertexBuilder.class)
public abstract class MixinSpriteTexturedVertexConsumer implements VertexDrain {
    @Shadow
    @Final
    private TextureAtlasSprite sprite;

    @Shadow
    @Final
    private IVertexBuilder delegate;

    @SuppressWarnings("unchecked")
    @Override
    public <T extends VertexSink> T createSink(VertexType<T> type) {
        if (type == VanillaVertexTypes.QUADS) {
            return (T) new SpriteTexturedVertexTransformer.Quad(VertexDrain.of(this.delegate)
                    .createSink(VanillaVertexTypes.QUADS), this.sprite);
        } else if (type == VanillaVertexTypes.PARTICLES) {
            return (T) new SpriteTexturedVertexTransformer.Particle(VertexDrain.of(this.delegate)
                    .createSink(VanillaVertexTypes.PARTICLES), this.sprite);
        } else if (type == VanillaVertexTypes.GLYPHS) {
            return (T) new SpriteTexturedVertexTransformer.Glyph(VertexDrain.of(this.delegate)
                    .createSink(VanillaVertexTypes.GLYPHS), this.sprite);
        }

        return type.createFallbackWriter((IVertexBuilder) this);
    }
}
