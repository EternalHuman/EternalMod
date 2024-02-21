package ru.zefirka.jcmod.mixins.perf.quad;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.model.quad.ModelQuadView;
import ru.zefirka.jcmod.utils.quad.ModelQuadFlags;

import static ru.zefirka.jcmod.utils.quad.ModelQuadUtil.*;

@Mixin(BakedQuad.class)
public class MixinBakedQuad implements ModelQuadView {
    @Shadow
    @Final
    protected int[] vertices;

    @Shadow
    @Final
    protected TextureAtlasSprite sprite;

    @Shadow
    @Final
    protected int tintIndex;

    private int cachedFlags;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(int[] vertexData, int colorIndex, Direction face, TextureAtlasSprite sprite, boolean shade, CallbackInfo ci) {
        this.cachedFlags = ModelQuadFlags.getQuadFlags((BakedQuad) (Object) this);
    }

    @Override
    public float getX(int idx) {
        return Float.intBitsToFloat(this.vertices[vertexOffset(idx) + POSITION_INDEX]);
    }

    @Override
    public float getY(int idx) {
        return Float.intBitsToFloat(this.vertices[vertexOffset(idx) + POSITION_INDEX + 1]);
    }

    @Override
    public float getZ(int idx) {
        return Float.intBitsToFloat(this.vertices[vertexOffset(idx) + POSITION_INDEX + 2]);
    }

    @Override
    public int getColor(int idx) {
    	if(vertexOffset(idx) + COLOR_INDEX < vertices.length) {
            return this.vertices[vertexOffset(idx) + COLOR_INDEX];
        } else
        {
            return vertices.length;
        }
    }

    @Override
    public TextureAtlasSprite rubidium$getSprite() {
        return this.sprite;
    }

    @Override
    public float getTexU(int idx) {
        return Float.intBitsToFloat(this.vertices[vertexOffset(idx) + TEXTURE_INDEX]);
    }

    @Override
    public float getTexV(int idx) {
        return Float.intBitsToFloat(this.vertices[vertexOffset(idx) + TEXTURE_INDEX + 1]);
    }

    @Override
    public int getFlags() {
        return this.cachedFlags;
    }

    @Override
    public int getLight(int idx) {
        return this.vertices[vertexOffset(idx) + LIGHT_INDEX];
    }

    @Override
    public int getNormal(int idx) {
        return this.vertices[vertexOffset(idx) + NORMAL_INDEX];
    }

    @Override
    public int getTintIndex() {
        return this.tintIndex;
    }
}
