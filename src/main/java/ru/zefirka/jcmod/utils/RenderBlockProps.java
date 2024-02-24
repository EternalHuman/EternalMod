package ru.zefirka.jcmod.utils;

import com.google.common.base.Objects;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import ru.zefirka.jcmod.utils.color.ColorABGR;
import javax.annotation.concurrent.Immutable;
import static ru.zefirka.jcmod.utils.RenderUtils.OPACITY;

@Immutable @Getter
public class RenderBlockProps {
    private final int color;
    private final BlockPos pos;

    public RenderBlockProps(BlockPos pos, int color) {
        this.pos = pos;
        final float red = (color >> 16 & 0xff) / 255f;
        final float green = (color >> 8 & 0xff) / 255f;
        final float blue = (color & 0xff) / 255f;

        this.color = ColorABGR.pack(red, green, blue, OPACITY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderBlockProps that = (RenderBlockProps) o;
        return Objects.equal(this.pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.pos);
    }
}