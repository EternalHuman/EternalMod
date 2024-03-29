package ru.zefirka.jcmod.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.culling.Cullable;
import ru.zefirka.jcmod.culling.DebugStats;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {

    @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
    public <E extends TileEntity> void render(E blockEntity, float f, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, CallbackInfo info) {
        TileEntityRenderer<E> blockEntityRenderer = getRenderer(blockEntity);
        if (blockEntityRenderer != null && blockEntityRenderer.shouldRenderOffScreen(blockEntity) && !((Cullable) blockEntity).isOffScreen()) {
            DebugStats.renderedBlockEntities++;
            return;
        }
        if (!((Cullable) blockEntity).isForcedVisible() && ((Cullable) blockEntity).isCulled()) {
            DebugStats.skippedBlockEntities++;
            info.cancel();
            return;
        }
        DebugStats.renderedBlockEntities++;
    }

    @Shadow
    public abstract <E extends TileEntity> TileEntityRenderer<E> getRenderer(E paramE);
}
