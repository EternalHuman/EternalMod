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

    private static final int STRIDE = 4;

    /**
     * @author EternalHuman
     * @reason Direct memory access
     */
    @Overwrite
    public void uploadInterpolatedFrame() {
        int curIndex = this$0.metadata.getFrameIndex(this$0.frame);
        int frameCount = this$0.metadata.getFrameCount() == 0 ? this$0.getFrameCount() : this$0.metadata.getFrameCount();
        int nextIndex = this$0.metadata.getFrameIndex((this$0.frame + 1) % frameCount);

        if (curIndex == nextIndex) {
            return;
        }

        float mix = 1.0F - (float) this$0.subFrame / (float) this$0.metadata.getFrameTime(this$0.frame);

        if (nextIndex >= 0 && nextIndex < this$0.getFrameCount()) {
            for(int layer = 0; layer < activeFrame.length; ++layer) {
                int width = this$0.info.width() >> layer;
                int height = this$0.info.height() >> layer;

                int curX = (this$0.framesX[curIndex] * width);
                int curY = (this$0.framesY[curIndex] * height);

                int nextX = (this$0.framesX[nextIndex] * width);
                int nextY = (this$0.framesY[nextIndex] * height);

                NativeImage src = this$0.mainImage[layer];
                NativeImage dst = activeFrame[layer];

                // Pointers to the pixel array for the current and next frame// Source pointers
                long pRgba1 = src.pixels + (curX + (long) curY * src.getWidth() * STRIDE);
                long pRgba2 = src.pixels + (nextX + (long) nextY * src.getWidth() * STRIDE);

                // Pointer to the pixel array where the interpolated results will be written
                long pInterpolatedRgba = dst.pixels;

                for (int pixelIndex = 0, pixelCount = width * height; pixelIndex < pixelCount; pixelIndex++) {
                    int rgba1 = MemoryUtil.memGetInt(pRgba1);
                    int rgba2 = MemoryUtil.memGetInt(pRgba2);

                    int mixedRgb = ColorMixer.mix(rgba1, rgba2, mix) & 0x00FFFFFF;
                    int alpha = rgba1 & 0xFF000000;

                    MemoryUtil.memPutInt(pInterpolatedRgba, mixedRgb | alpha);

                    pRgba1 += STRIDE;
                    pRgba2 += STRIDE;
                    pInterpolatedRgba += STRIDE;
                }
            }

            this$0.upload(0, 0, activeFrame);
        }

    }
}