package ru.zefirka.jcmod.mixins.perf.sprite;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.*;
import ru.zefirka.jcmod.utils.color.ColorMixer;

@Mixin(TextureAtlasSprite.InterpolationData.class)
public class MixinSpriteInterpolated {
    @Shadow
    @Final
    private NativeImage[] activeFrame;

    @Final
    @Shadow (aliases = "this$0")
    private TextureAtlasSprite this$0;

    @Unique
    private int frameCount = this$0.getHeight() * this$0.getWidth();

    private static final int STRIDE = 4;

    /**
     * @author EternalHuman
     * @reason Drastic optimizations
     */
    @Overwrite
    public void uploadInterpolatedFrame() {
        int curIndex = this$0.metadata.getFrameIndex(this$0.frame);
        int framesSize = this$0.metadata.getFrameCount() == 0 ? this$0.getFrameCount() : this$0.metadata.getFrameCount();
        int nextIndex = this$0.metadata.getFrameIndex((this$0.frame + 1) % framesSize);

        if (curIndex == nextIndex) {
            return;
        }
        float delta = 1.0F - (float) this$0.subFrame / (float) this$0.metadata.getFrameTime(this$0.frame);

        int f1 = ColorMixer.getStartRatio(delta);
        int f2 = ColorMixer.getEndRatio(delta);

        for (int layer = 0; layer < this.activeFrame.length; layer++) {
            int width = this.this$0.info.width() >> layer;
            int height = this.this$0.info.height() >> layer;

            int curX = ((curIndex % frameCount) * width);
            int curY = ((curIndex / frameCount) * height);

            int nextX = ((nextIndex % frameCount) * width);
            int nextY = ((nextIndex / frameCount) * height);

            NativeImage src = this.this$0.mainImage[layer];
            NativeImage dst = this.activeFrame[layer];

            // Source pointers
            long s1p = src.pixels + (curX + ((long) curY * src.getWidth()) * STRIDE);
            long s2p = src.pixels + (nextX + ((long) nextY * src.getWidth()) * STRIDE);

            // Destination pointers
            long dp = dst.pixels;

            int pixelCount = width * height;

            for (int i = 0; i < pixelCount; i++) {
                MemoryUtil.memPutInt(dp, ColorMixer.mixARGB(MemoryUtil.memGetInt(s1p), MemoryUtil.memGetInt(s2p), f1, f2));

                s1p += STRIDE;
                s2p += STRIDE;
                dp += STRIDE;
            }
        }

        this.this$0.upload(0, 0, this.activeFrame);
    }
}