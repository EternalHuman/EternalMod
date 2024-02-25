package ru.zefirka.jcmod.mixins.perf.signs;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.List;

import static net.minecraft.client.renderer.RenderType.*;

@Mixin(RenderType.class)
public abstract class MixinRenderLayer {
    private static final List<RenderType> layers =  ImmutableList.of(solid(), cutoutMipped(), cutout(), translucent(), tripwire());;

    /**
     * @author EternalHuman
     * @reason Cache it (Mojang omegalul)
     */
    @Overwrite
    public static List<RenderType> chunkBufferLayers() {
        return layers;
    }

}
