package ru.zefirka.jcmod.mixins.common.world;

import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import ru.zefirka.jcmod.lightoptimizer.common.world.ExtendedWorld;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld, AutoCloseable, ExtendedWorld {}
