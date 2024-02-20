package ru.zefirka.jcmod.mixins.perf.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.model.quad.ModelQuadView;
import ru.zefirka.jcmod.utils.DirectionUtil;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.color.ColorARGB;
import ru.zefirka.jcmod.utils.items.ItemColorsExtended;
import ru.zefirka.jcmod.utils.quad.ModelQuadUtil;
import ru.zefirka.jcmod.utils.random.XoRoShiRoRandom;
import ru.zefirka.jcmod.utils.sprite.SpriteUtil;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;

import java.util.List;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    private final XoRoShiRoRandom random = new XoRoShiRoRandom();

    @Shadow
    @Final
    private ItemColors itemColors;

    /**
     * @reason Avoid allocations
     * @author EternalHuman
     */
    @Overwrite
    public void renderModelLists(IBakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, IVertexBuilder vertices) {
        XoRoShiRoRandom random = this.random;

        for (Direction direction : DirectionUtil.ALL_DIRECTIONS) {
            List<BakedQuad> quads = model.getQuads(null, direction, random.setSeedAndReturn(42L));

            if (!quads.isEmpty()) {
                this.renderQuadList(matrices, vertices, quads, stack, light, overlay);
            }
        }

        List<BakedQuad> quads = model.getQuads(null, null, random.setSeedAndReturn(42L));

        if (!quads.isEmpty()) {
            this.renderQuadList(matrices, vertices, quads, stack, light, overlay);
        }
    }

    /**
     * @reason Use vertex building intrinsics
     * @author EternalHuman
     */
    @Overwrite
    public void renderQuadList(MatrixStack matrices, IVertexBuilder vertexConsumer, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
        MatrixStack.Entry entry = matrices.last();

        IItemColor colorProvider = null;

        QuadVertexSink drain = VertexDrain.of(vertexConsumer)
                .createSink(VanillaVertexTypes.QUADS);
        drain.ensureCapacity(quads.size() * 4);

        for (BakedQuad bakedQuad : quads) {
            int color = 0xFFFFFFFF;

            if (!stack.isEmpty() && bakedQuad.isTinted()) {
                if (colorProvider == null) {
                    colorProvider = ((ItemColorsExtended) this.itemColors).getColorProvider(stack);
                }

                color = ColorARGB.toABGR(colorProvider != null ? colorProvider.getColor(stack, bakedQuad.getTintIndex()) :
                        this.itemColors.getColor(stack, bakedQuad.getTintIndex()), 255);
            }

            ModelQuadView quad = ((ModelQuadView) bakedQuad);

            for (int i = 0; i < 4; i++) {
                int fColor = multABGRInts(quad.getColor(i), color);

                drain.writeQuad(entry, quad.getX(i), quad.getY(i), quad.getZ(i), fColor, quad.getTexU(i), quad.getTexV(i),
                        light, overlay, ModelQuadUtil.getFacingNormal(bakedQuad.getDirection()));
            }

            SpriteUtil.markSpriteActive(quad.rubidium$getSprite());
        }

        drain.flush();
    }

    private int multABGRInts(int colorA, int colorB) {
        // Most common case: Either quad coloring or tint-based coloring, but not both
        if (colorA == -1) {
            return colorB;
        } else if (colorB == -1) {
            return colorA;
        }
        // General case (rare): Both colorings, actually perform the multiplication
        int a = (int)((ColorABGR.unpackAlpha(colorA)/255.0f) * (ColorABGR.unpackAlpha(colorB)/255.0f) * 255.0f);
        int b = (int)((ColorABGR.unpackBlue(colorA)/255.0f) * (ColorABGR.unpackBlue(colorB)/255.0f) * 255.0f);
        int g = (int)((ColorABGR.unpackGreen(colorA)/255.0f) * (ColorABGR.unpackGreen(colorB)/255.0f) * 255.0f);
        int r = (int)((ColorABGR.unpackRed(colorA)/255.0f) * (ColorABGR.unpackRed(colorB)/255.0f) * 255.0f);
        return ColorABGR.pack(r, g, b, a);
    }

}
