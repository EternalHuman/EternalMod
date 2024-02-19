package ru.zefirka.jcmod.utils;

import com.google.common.base.Objects;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

import javax.annotation.concurrent.Immutable;

@Immutable @Getter
public class RenderBlockProps {
    private final int color;
    private final BlockPos pos;

    public RenderBlockProps(BlockPos pos, int color) {
        this.pos = pos;
        this.color = color;
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