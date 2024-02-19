package ru.zefirka.jcmod.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import ru.zefirka.jcmod.culling.CullableType;

@Mixin(value = { EntityType.class, TileEntityType.class})
public class CullableEntityType implements CullableType {
    private boolean cullWhitelisted;

    @Override
    public boolean isCullWhitelisted() {
        return this.cullWhitelisted;
    }

    @Override
    public void setCullWhitelisted(boolean cullWhitelisted) {
        this.cullWhitelisted = cullWhitelisted;
    }
}
