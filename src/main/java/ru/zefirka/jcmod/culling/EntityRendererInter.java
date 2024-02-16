package ru.zefirka.jcmod.culling;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.text.ITextComponent;

public interface EntityRendererInter<T extends net.minecraft.entity.Entity> {
	boolean shadowShouldShowName(T paramT);

	void shadowRenderNameTag(T paramT, ITextComponent paramITextComponent, MatrixStack paramMatrixStack, IRenderTypeBuffer paramIRenderTypeBuffer, int paramInt);
}