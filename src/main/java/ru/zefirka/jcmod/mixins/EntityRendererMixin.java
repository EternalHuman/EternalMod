package ru.zefirka.jcmod.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.culling.EntityRendererInter;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> implements EntityRendererInter<T> {

	@Override
	public boolean shadowShouldShowName(T entity) {
		return shouldShowName(entity);
	}

	@Override
	public void shadowRenderNameTag(T entity, ITextComponent component, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int light) {
	    renderNameTag(entity, component, poseStack, multiBufferSource, light);
	}

	@Shadow
	public abstract boolean shouldShowName(T entity);

	@Shadow
	public abstract void renderNameTag(T entity, ITextComponent component, MatrixStack poseStack,
									   IRenderTypeBuffer multiBufferSource, int i);

	/**
	 * @author EternalHuman
	 * @reason FUCK OPTIFINE!
	 */
	@Overwrite
	public boolean shouldRender(T p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
		if (!p_225626_1_.shouldRender(p_225626_3_, p_225626_5_, p_225626_7_)) {
			return false;
		} else if (p_225626_1_.noCulling) {
			return true;
		} else {
			AxisAlignedBB axisalignedbb = p_225626_1_.getBoundingBoxForCulling().inflate(0.5D);
			if (axisalignedbb.hasNaN() || axisalignedbb.getSize() == 0.0D) {
				axisalignedbb = new AxisAlignedBB(p_225626_1_.getX() - 2.0D, p_225626_1_.getY() - 2.0D, p_225626_1_.getZ() - 2.0D, p_225626_1_.getX() + 2.0D, p_225626_1_.getY() + 2.0D, p_225626_1_.getZ() + 2.0D);
			}

			return p_225626_2_.isVisible(axisalignedbb);
		}
	}
}
