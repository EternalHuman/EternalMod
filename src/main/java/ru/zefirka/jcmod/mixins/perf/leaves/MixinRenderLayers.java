package ru.zefirka.jcmod.mixins.perf.leaves;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.*;
import java.util.Map;

@Mixin(RenderTypeLookup.class)
public class MixinRenderLayers {
    @Mutable
    @Shadow
    @Final
    private static Map<Block, RenderType> TYPE_BY_BLOCK;

    @Mutable
    @Shadow
    @Final
    private static Map<Fluid, RenderType> TYPE_BY_FLUID;

    static {
        // Replace the backing collection types with something a bit faster, since this is a hot spot in chunk rendering.
        TYPE_BY_BLOCK = new Reference2ReferenceOpenHashMap<>(TYPE_BY_BLOCK);
        TYPE_BY_FLUID = new Reference2ReferenceOpenHashMap<>(TYPE_BY_FLUID);
    }
}
