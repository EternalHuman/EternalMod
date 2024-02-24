package ru.zefirka.jcmod.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.culling.Cullable;
import ru.zefirka.jcmod.culling.DebugStats;
import ru.zefirka.jcmod.culling.EntityRendererInter;
import ru.zefirka.jcmod.culling.EternalOptimizer;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import ru.zefirka.jcmod.utils.math.Matrix4fExtended;
import ru.zefirka.jcmod.utils.math.MatrixUtil;
import ru.zefirka.jcmod.utils.vertex.VanillaVertexTypes;
import ru.zefirka.jcmod.utils.vertex.VertexDrain;
import ru.zefirka.jcmod.utils.vertex.formats.line.LineVertexSink;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private EntityRendererManager entityRenderDispatcher;

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta,
                              MatrixStack matrices, IRenderTypeBuffer vertexConsumers, CallbackInfo info) {
        Cullable cullable = (Cullable) entity;
        if (!cullable.isForcedVisible() && cullable.isCulled()) {
            EntityRenderer<Entity> entityRenderer = (EntityRenderer<Entity>) entityRenderDispatcher.getRenderer(entity);
            EntityRendererInter<Entity> entityRendererInter = (EntityRendererInter<Entity>) entityRenderer;
            if (EternalOptimizer.getEternalModConfig().renderNametagsThroughWalls && matrices != null
                    && vertexConsumers != null && entityRendererInter.shadowShouldShowName(entity)) {
                double x = MathHelper.lerp(tickDelta, entity.xOld, entity.getX()) - cameraX;
                double y = MathHelper.lerp(tickDelta, entity.yOld, entity.getY()) - cameraY;
                double z = MathHelper.lerp(tickDelta, entity.zOld, entity.getZ()) - cameraZ;
                Vector3d vec3d = entityRenderer.getRenderOffset(entity, tickDelta);
                double d = x + vec3d.x;
                double e = y + vec3d.y;
                double f = z + vec3d.z;
                matrices.pushPose();
                matrices.translate(d, e, f);
                entityRendererInter.shadowRenderNameTag(entity, entity.getDisplayName(), matrices,
                        vertexConsumers, this.entityRenderDispatcher.getPackedLightCoords(entity, tickDelta));
                matrices.popPose();
            }
            DebugStats.skippedEntities++;
            info.cancel();
            return;
        }
        DebugStats.renderedEntities++;
    }


    /**
     * @author EternalHuman
     * @reason Use intrinsics where possible to speed up vertex writing
     */
    @Overwrite
    public static void renderLineBox(MatrixStack matrices, IVertexBuilder vertexConsumer, double x1, double y1, double z1,
                               double x2, double y2, double z2, float red, float green, float blue, float alpha,
                               float xAxisRed, float yAxisGreen, float zAxisBlue) {
        Matrix4f model = matrices.last().pose();

        float x1f = (float) x1;
        float y1f = (float) y1;
        float z1f = (float) z1;
        float x2f = (float) x2;
        float y2f = (float) y2;
        float z2f = (float) z2;

        int color = ColorABGR.pack(red, green, blue, alpha);

        Matrix4fExtended matrixExt = MatrixUtil.getExtendedMatrix(model);

        float v1x = matrixExt.transformVecX(x1f, y1f, z1f);
        float v1y = matrixExt.transformVecY(x1f, y1f, z1f);
        float v1z = matrixExt.transformVecZ(x1f, y1f, z1f);

        float v2x = matrixExt.transformVecX(x2f, y1f, z1f);
        float v2y = matrixExt.transformVecY(x2f, y1f, z1f);
        float v2z = matrixExt.transformVecZ(x2f, y1f, z1f);

        float v3x = matrixExt.transformVecX(x1f, y2f, z1f);
        float v3y = matrixExt.transformVecY(x1f, y2f, z1f);
        float v3z = matrixExt.transformVecZ(x1f, y2f, z1f);

        float v4x = matrixExt.transformVecX(x1f, y1f, z2f);
        float v4y = matrixExt.transformVecY(x1f, y1f, z2f);
        float v4z = matrixExt.transformVecZ(x1f, y1f, z2f);

        float v5x = matrixExt.transformVecX(x2f, y2f, z1f);
        float v5y = matrixExt.transformVecY(x2f, y2f, z1f);
        float v5z = matrixExt.transformVecZ(x2f, y2f, z1f);

        float v6x = matrixExt.transformVecX(x1f, y2f, z2f);
        float v6y = matrixExt.transformVecY(x1f, y2f, z2f);
        float v6z = matrixExt.transformVecZ(x1f, y2f, z2f);

        float v7x = matrixExt.transformVecX(x2f, y1f, z2f);
        float v7y = matrixExt.transformVecY(x2f, y1f, z2f);
        float v7z = matrixExt.transformVecZ(x2f, y1f, z2f);

        float v8x = matrixExt.transformVecX(x2f, y2f, z2f);
        float v8y = matrixExt.transformVecY(x2f, y2f, z2f);
        float v8z = matrixExt.transformVecZ(x2f, y2f, z2f);

        LineVertexSink lines = VertexDrain.of(vertexConsumer)
                .createSink(VanillaVertexTypes.LINES);
        lines.ensureCapacity(24);

        lines.vertexLine(v1x, v1y, v1z, red, yAxisGreen, zAxisBlue, alpha);
        lines.vertexLine(v2x, v2y, v2z, red, yAxisGreen, zAxisBlue, alpha);

        lines.vertexLine(v1x, v1y, v1z, xAxisRed, green, zAxisBlue, alpha);
        lines.vertexLine(v3x, v3y, v3z, xAxisRed, green, zAxisBlue, alpha);

        lines.vertexLine(v1x, v1y, v1z, xAxisRed, yAxisGreen, blue, alpha);
        lines.vertexLine(v4x, v4y, v4z, xAxisRed, yAxisGreen, blue, alpha);

        lines.vertexLine(v2x, v2y, v2z, color);
        lines.vertexLine(v5x, v5y, v5z, color);

        lines.vertexLine(v5x, v5y, v5z, color);
        lines.vertexLine(v3x, v3y, v3z, color);

        lines.vertexLine(v3x, v3y, v3z, color);
        lines.vertexLine(v6x, v6y, v6z, color);

        lines.vertexLine(v6x, v6y, v6z, color);
        lines.vertexLine(v4x, v4y, v4z, color);

        lines.vertexLine(v4x, v4y, v4z, color);
        lines.vertexLine(v7x, v7y, v7z, color);

        lines.vertexLine(v7x, v7y, v7z, color);
        lines.vertexLine(v2x, v2y, v2z, color);

        lines.vertexLine(v6x, v6y, v6z, color);
        lines.vertexLine(v8x, v8y, v8z, color);

        lines.vertexLine(v7x, v7y, v7z, color);
        lines.vertexLine(v8x, v8y, v8z, color);

        lines.vertexLine(v5x, v5y, v5z, color);
        lines.vertexLine(v8x, v8y, v8z, color);

        lines.flush();
    }

}