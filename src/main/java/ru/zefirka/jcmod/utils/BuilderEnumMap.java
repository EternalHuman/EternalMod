package ru.zefirka.jcmod.utils;

import java.util.EnumMap;
import java.util.Map;

public class BuilderEnumMap<K extends Enum<K>, V> extends EnumMap<K, V> {
    public BuilderEnumMap(Class<K> keyType) {
        super(keyType);
    }

    public BuilderEnumMap(EnumMap<K, ? extends V> m) {
        super(m);
    }

    public BuilderEnumMap(Map<K, ? extends V> m) {
        super(m);
    }

    public BuilderEnumMap<K, V> append(K key, V value) {
        this.put(key, value);
        return this;
    }

    public static <K extends Enum<K>, V> BuilderEnumMap<K, V> builder(Class<K> type) {
        return new BuilderEnumMap<>(type);
    }

    public static <K extends Enum<K>> BuilderEnumMap<K, String> stringsBuilder(Class<K> type) {
        return new BuilderEnumMap<>(type);
    }
}
