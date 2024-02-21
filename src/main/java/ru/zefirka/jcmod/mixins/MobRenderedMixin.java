package ru.zefirka.jcmod.mixins;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MobRenderer.class)
public abstract class MobRenderedMixin<T extends MobEntity, M extends EntityModel<T>> extends LivingRenderer<T, M>  {

    public MobRenderedMixin(EntityRendererManager p_i50965_1_, M p_i50965_2_, float p_i50965_3_) {
        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
    }

    /**
     * @author EternalHuman
     * @reason FUCK OPTIFINE 2!
     */
    @Overwrite
    public boolean shouldRender(T p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        if (super.shouldRender(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)) {
            return true;
        } else {
            Entity entity = p_225626_1_.getLeashHolder();
            return entity != null ? p_225626_2_.isVisible(entity.getBoundingBoxForCulling()) : false;
        }
    }
}
