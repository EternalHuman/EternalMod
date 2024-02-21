package ru.zefirka.jcmod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.ParticleEffectAmbience;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zefirka.jcmod.culling.DebugStats;
import ru.zefirka.jcmod.utils.random.XoRoShiRoRandom;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    public void tickEntity(Entity entity, CallbackInfo info) {
        DebugStats.tickedEntities++;
    }

    @Shadow
    protected abstract void trySpawnDripParticles(BlockPos pos, BlockState state, IParticleData parameters, boolean bl);

    protected ClientWorldMixin(ClientWorld.ClientWorldInfo mutableWorldProperties, RegistryKey<World> registryKey,
                               DimensionType dimensionType, Supplier<IProfiler> profiler, boolean bl, boolean bl2, long l) {
        super(mutableWorldProperties, registryKey, dimensionType, profiler, bl, bl2, l);
    }

    @Redirect(method = "animateTick", at = @At(value = "NEW", target = "()Ljava/util/Random;"))
    private Random redirectRandomTickRandom() {
        return new XoRoShiRoRandom();
    }

    /**
     * @reason Avoid allocations, branch code out, early-skip some code
     * @author EternalHuman
     */
    @Overwrite
    public void doAnimateTick(int xCenter, int yCenter, int zCenter, int radius, Random random, boolean spawnBarrierParticles, BlockPos.Mutable pos) {
        int x = xCenter + (random.nextInt(radius) - random.nextInt(radius));
        int y = yCenter + (random.nextInt(radius) - random.nextInt(radius));
        int z = zCenter + (random.nextInt(radius) - random.nextInt(radius));

        pos.set(x, y, z);

        BlockState blockState = this.getBlockState(pos);

        if (!blockState.isAir()) {
            blockState.getBlock().animateTick(blockState, this, pos, random);
            this.performBarrierTick(blockState, pos, spawnBarrierParticles);
        }

        if (!blockState.isCollisionShapeFullBlock(this, pos)) {
            this.performBiomeParticleDisplayTick(pos, random);
        }

        FluidState fluidState = blockState.getFluidState();

        if (!fluidState.isEmpty()) {
            fluidState.animateTick(this, pos, random);
            this.performFluidParticles(blockState, fluidState, pos, random);
        }
    }

    private void performBarrierTick(BlockState blockState, BlockPos pos, boolean spawnBarrierParticles) {
        if (spawnBarrierParticles && blockState.is(Blocks.BARRIER)) {
            this.performBarrierDisplayTick(pos);
        }
    }

    private void performBarrierDisplayTick(BlockPos pos) {
        this.addParticle(ParticleTypes.BARRIER, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                0.0D, 0.0D, 0.0D);
    }

    private void performBiomeParticleDisplayTick(BlockPos pos, Random random) {
        ParticleEffectAmbience config = this.getBiome(pos)
                .getAmbientParticle()
                .orElse(null);

        if (config != null && config.canSpawn(random)) {
            this.addParticle(config.getOptions(),
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + random.nextDouble(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    private void performFluidParticles(BlockState blockState, FluidState fluidState, BlockPos.Mutable pos, Random random) {
        IParticleData particleEffect = fluidState.getDripParticle();

        if (particleEffect != null && random.nextInt(10) == 0) {
            boolean solid = blockState.isFaceSturdy(this, pos, Direction.DOWN);

            pos.setY(pos.getY() - 1);

            this.trySpawnDripParticles(pos, this.getBlockState(pos), particleEffect, solid);
        }
    }
}
