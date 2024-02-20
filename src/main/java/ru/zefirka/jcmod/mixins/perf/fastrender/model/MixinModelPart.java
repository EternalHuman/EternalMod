package ru.zefirka.jcmod.mixins.perf.fastrender.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.math.Matrix3fExtended;
import ru.zefirka.jcmod.utils.math.Matrix4fExtended;
import ru.zefirka.jcmod.utils.math.MatrixUtil;
import ru.zefirka.jcmod.utils.render.ModelCuboidAccessor;
import ru.zefirka.jcmod.utils.render.Norm3b;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.formats.quad.QuadVertexSink;

@Mixin(ModelRenderer.class)
public class MixinModelPart {
    private static final float NORM = 1.0F / 16.0F;

    @Shadow
    @Final
    private ObjectList<ModelRenderer.ModelBox> cubes;

    /**
     * @author EternalHuman
     * @reason Use optimized vertex writer, avoid allocations, use quick matrix transformations
     */
    @Overwrite
    private void compile(MatrixStack.Entry matrices, IVertexBuilder vertexBuilder, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix3fExtended normalExt = MatrixUtil.getExtendedMatrix(matrices.normal());
        Matrix4fExtended modelExt = MatrixUtil.getExtendedMatrix(matrices.pose());

        QuadVertexSink drain = VertexDrain.of(vertexBuilder).createSink(VanillaVertexTypes.QUADS);
        drain.ensureCapacity(this.cubes.size() * 6 * 4);

        int color = ColorABGR.pack(red, green, blue, alpha);

        for (ModelRenderer.ModelBox cuboid : this.cubes) {
            for (ModelRenderer.TexturedQuad quad : ((ModelCuboidAccessor) cuboid).getQuads()) {
                float normX = normalExt.transformVecX(quad.normal);
                float normY = normalExt.transformVecY(quad.normal);
                float normZ = normalExt.transformVecZ(quad.normal);

                int norm = Norm3b.pack(normX, normY, normZ);

                for (ModelRenderer.PositionTextureVertex vertex : quad.vertices) {
                    Vector3f pos = vertex.pos;

                    float x1 = pos.x() * NORM;
                    float y1 = pos.y() * NORM;
                    float z1 = pos.z() * NORM;

                    float x2 = modelExt.transformVecX(x1, y1, z1);
                    float y2 = modelExt.transformVecY(x1, y1, z1);
                    float z2 = modelExt.transformVecZ(x1, y1, z1);

                    drain.writeQuad(x2, y2, z2, color, vertex.u, vertex.v, light, overlay, norm);
                }
            }
        }

        drain.flush();
    }
}
