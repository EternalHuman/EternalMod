package ru.zefirka.jcmod.mixins.perf.matrix;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.zefirka.jcmod.utils.math.Matrix3fExtended;
import ru.zefirka.jcmod.utils.math.Matrix4fExtended;
import ru.zefirka.jcmod.utils.math.MatrixUtil;

@Mixin(IVertexBuilder.class)
public interface MixinVertexConsumer {
    @Shadow
    IVertexBuilder normal(float x, float y, float z);

    @Shadow
    IVertexBuilder vertex(double x, double y, double z);

    /**
     * @reason Avoid allocations
     * @author EternalHuman
     */
    @Overwrite
    default IVertexBuilder vertex(Matrix4f matrix, float x, float y, float z) {
        Matrix4fExtended ext = MatrixUtil.getExtendedMatrix(matrix);
        float x2 = ext.transformVecX(x, y, z);
        float y2 = ext.transformVecY(x, y, z);
        float z2 = ext.transformVecZ(x, y, z);

        return this.vertex(x2, y2, z2);
    }

    /**
     * @reason Avoid allocations
     * @author EternalHuman
     */
    @Overwrite
    default IVertexBuilder normal(Matrix3f matrix, float x, float y, float z) {
        Matrix3fExtended ext = MatrixUtil.getExtendedMatrix(matrix);
        float x2 = ext.transformVecX(x, y, z);
        float y2 = ext.transformVecY(x, y, z);
        float z2 = ext.transformVecZ(x, y, z);

        return this.normal(x2, y2, z2);
    }
}
