package ru.zefirka.jcmod.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.culling.DebugStats;
import ru.zefirka.jcmod.culling.EternalOptimizer;
import ru.zefirka.jcmod.culling.Cullable;
import ru.zefirka.jcmod.culling.EntityRendererInter;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private EntityRendererManager entityRenderDispatcher;

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta,
                              MatrixStack matrices, IRenderTypeBuffer vertexConsumers, CallbackInfo info) {
        Cullable cullable = (Cullable) entity;
        if (!cullable.isCheckTimeout() && cullable.isCulled() && !entity.noCulling) {
            @SuppressWarnings("unchecked")
            EntityRenderer<Entity> entityRenderer = (EntityRenderer<Entity>) entityRenderDispatcher.getRenderer(entity);
            @SuppressWarnings("unchecked")
            EntityRendererInter<Entity> entityRendererInter = (EntityRendererInter<Entity>) entityRenderer;
            if (EternalOptimizer.getInstance().config.renderNametagsThroughWalls && matrices != null
                    && vertexConsumers != null && entityRendererInter.shadowShouldShowName(entity)) {
                double x = MathHelper.lerp(tickDelta, entity.xOld, entity.getX())
                        - cameraX;
                double y = MathHelper.lerp(tickDelta, entity.yOld, entity.getY())
                        - cameraY;
                double z = MathHelper.lerp(tickDelta, entity.zOld, entity.getZ())
                        - cameraZ;
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

}