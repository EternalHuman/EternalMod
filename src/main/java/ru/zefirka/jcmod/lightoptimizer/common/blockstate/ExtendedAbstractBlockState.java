package ru.zefirka.jcmod.lightoptimizer.common.blockstate;

public interface ExtendedAbstractBlockState {

    public boolean isConditionallyFullOpaque();

    public int getOpacityIfCached();

}
