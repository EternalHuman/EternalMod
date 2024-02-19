package ru.zefirka.jcmod.mixins;

import java.util.List;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zefirka.jcmod.culling.DebugStats;
import ru.zefirka.jcmod.culling.EternalOptimizer;

@Mixin(DebugOverlayGui.class)
public class DebugHudMixin {

    private int lastTickedEntities = 0;
    private int lastSkippedEntityTicks = 0;

    @Inject(method = "getGameInformation", at = @At("RETURN"))
    public List<String> getLeftText(CallbackInfoReturnable<List<String>> callback) {
        if(DebugStats.tickedEntities != 0 || DebugStats.skippedEntityTicks != 0) {
            lastTickedEntities = DebugStats.tickedEntities;
            lastSkippedEntityTicks = DebugStats.skippedEntityTicks;
            DebugStats.tickedEntities = 0;
            DebugStats.skippedEntityTicks = 0;
        }
        List<String> list = callback.getReturnValue();
        list.add("[EternalMod] Last pass: " + DebugStats.lastTime + "ms");
        list.add("[EternalMod] Rendered Block Entities: " + DebugStats.renderedBlockEntities
                + " Skipped: " + DebugStats.skippedBlockEntities);
        list.add("[EternalMod] Rendered Entities: " + DebugStats.renderedEntities + " Skipped: "
                + DebugStats.skippedEntities);
        list.add("[EternalMod] Ticked Entities: " + lastTickedEntities + " Skipped: " + lastSkippedEntityTicks);
        list.add("[EternalMod] OutOfCamera: " + DebugStats.outOfCameraEntities);

        DebugStats.renderedBlockEntities = 0;
        DebugStats.skippedBlockEntities = 0;
        DebugStats.renderedEntities = 0;
        DebugStats.skippedEntities = 0;

        return list;
    }

}