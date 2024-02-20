package ru.zefirka.jcmod.mixins.perf.sprite;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.utils.sprite.SpriteExtended;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinSprite implements SpriteExtended {
    private boolean forceNextUpdate;

    @Shadow
    private int subFrame;

    @Shadow
    @Final
    private AnimationMetadataSection metadata;

    @Shadow
    private int frame;

    @Shadow
    public abstract int getFrameCount();

    @Shadow
    protected abstract void upload(int int_1);

    @Shadow
    @Final
    private TextureAtlasSprite.InterpolationData interpolationData;

    /**
     * @author EternalHuman
     * @reason Allow conditional texture updating
     */
    @Overwrite
    public void cycleFrames() {
        this.subFrame++;


        if (this.forceNextUpdate) {
            this.uploadTexture();
        }
    }

    private void uploadTexture() {
        if (this.subFrame >= this.metadata.getFrameTime(this.frame)) {
            int prevFrameIndex = this.metadata.getFrameIndex(this.frame);
            int frameCount = this.metadata.getFrameCount() == 0 ? this.getFrameCount() : this.metadata.getFrameCount();

            this.frame = (this.frame + 1) % frameCount;
            this.subFrame = 0;

            int frameIndex = this.metadata.getFrameIndex(this.frame);

            if (prevFrameIndex != frameIndex && frameIndex >= 0 && frameIndex < this.getFrameCount()) {
                this.upload(frameIndex);
            }
        } else if (this.interpolationData != null) {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(this::updateInterpolatedTexture);
            } else {
                this.updateInterpolatedTexture();
            }
        }

        this.forceNextUpdate = false;
    }

    @Override
    public void markActive() {
        this.forceNextUpdate = true;
    }

    private void updateInterpolatedTexture() {
        this.interpolationData.uploadInterpolatedFrame();
    }
}
