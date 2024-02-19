package ru.zefirka.jcmod.culling;

public interface CullableType {
    boolean isCullWhitelisted();

    void setCullWhitelisted(boolean whitelisted);
}
