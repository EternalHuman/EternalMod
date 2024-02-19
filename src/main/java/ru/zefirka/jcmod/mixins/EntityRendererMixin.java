package ru.zefirka.jcmod.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
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
	
}
