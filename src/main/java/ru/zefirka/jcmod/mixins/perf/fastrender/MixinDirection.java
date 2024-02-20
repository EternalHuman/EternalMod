package ru.zefirka.jcmod.mixins.perf.fastrender;

import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Direction.class)
public class MixinDirection {
    /**
     * @author EternalHuman
     * @reason Avoid looping over all directions and computing the dot product
     */
    @Overwrite
    public static Direction getNearest(float x, float y, float z) {
        // First choice in ties: negative, positive; Y, Z, X
        float yM = Math.abs(y);
        float zM = Math.abs(z);
        float xM = Math.abs(x);

        if (yM >= zM) {
            if (yM >= xM) {
                // Y biggest
                if (y <= 0) {
                    return Direction.DOWN;
                } else /* y > 0 */ {
                    return Direction.UP;
                }
            } else /* zM <= yM < xM */ {
                // X biggest, fall through
            }
        } else /* yM < zM */ {
            if (zM >= xM) {
                // Z biggest
                if (z <= 0) {
                    return Direction.NORTH;
                } else /* z > 0 */ {
                    return Direction.SOUTH;
                }
            } else /* yM < zM < xM */ {
                // X biggest, fall through
            }
        }

        // X biggest
        if (x <= 0) {
            return Direction.WEST;
        } else /* x > 0 */ {
            return Direction.EAST;
        }
    }
}