package ru.zefirka.jcmod.mixins.perf.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ru.zefirka.jcmod.model.quad.ModelQuadView;
import ru.zefirka.jcmod.utils.DirectionUtil;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.quad.ModelQuadUtil;
import ru.zefirka.jcmod.utils.random.XoRoShiRoRandom;
import ru.zefirka.jcmod.utils.sprite.SpriteUtil;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {
    private final XoRoShiRoRandom random = new XoRoShiRoRandom();


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void renderModel(MatrixStack.Entry entry, IVertexBuilder vertexConsumer, @Nullable BlockState blockState, IBakedModel bakedModel, float red, float green, float blue, int light, int overlay, net.minecraftforge.client.model.data.IModelData modelData) {
        QuadVertexSink drain = VertexDrain.of(vertexConsumer)
                .createSink(VanillaVertexTypes.QUADS);
        XoRoShiRoRandom random = this.random;

        if (modelData == null) {
            modelData = EmptyModelData.INSTANCE;
        }

        // Clamp color ranges
        red = MathHelper.clamp(red, 0.0F, 1.0F);
        green = MathHelper.clamp(green, 0.0F, 1.0F);
        blue = MathHelper.clamp(blue, 0.0F, 1.0F);

        int defaultColor = ColorABGR.pack(red, green, blue, 1.0F);

        for (Direction direction : DirectionUtil.ALL_DIRECTIONS) {
            List<BakedQuad> quads = bakedModel.getQuads(blockState, direction, random.setSeedAndReturn(42L), modelData);

            if (!quads.isEmpty()) {
                renderQuad(entry, drain, defaultColor, quads, light, overlay);
            }
        }

        List<BakedQuad> quads = bakedModel.getQuads(blockState, null, random.setSeedAndReturn(42L), modelData);

        if (!quads.isEmpty()) {
            renderQuad(entry, drain, defaultColor, quads, light, overlay);
        }

        drain.flush();
    }

    private static void renderQuad(MatrixStack.Entry entry, QuadVertexSink drain, int defaultColor, List<BakedQuad> list, int light, int overlay) {
        if (list.isEmpty()) {
            return;
        }

        drain.ensureCapacity(list.size() * 4);

        for (BakedQuad bakedQuad : list) {
            int color = bakedQuad.isTinted() ? defaultColor : 0xFFFFFFFF;

            ModelQuadView quad = ((ModelQuadView) bakedQuad);

            for (int i = 0; i < 4; i++) {
                drain.writeQuad(entry, quad.getX(i), quad.getY(i), quad.getZ(i), color, quad.getTexU(i), quad.getTexV(i),
                        light, overlay, ModelQuadUtil.getFacingNormal(bakedQuad.getDirection()));
            }

            SpriteUtil.markSpriteActive(quad.rubidium$getSprite());
        }
    }
}
