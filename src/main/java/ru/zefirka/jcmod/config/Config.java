package ru.zefirka.jcmod.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Config {

    public int configVersion = 1;
    public boolean renderNametagsThroughWalls = true;
    public Set<String> blockEntityWhitelist = new HashSet<>(Arrays.asList("minecraft:beacon"));
    public Set<String> entityWhitelist = new HashSet<>();
    public int cullingEntitiesDistance = 120;
    public int cullingTileDistance = 100;
    public boolean debugMode = false;
    public int sleepDelay = 15;
    public int hitboxLimit = 50;
    public boolean skipMarkerArmorStands = true;
    public boolean tickCulling = true;
    public Set<String> tickCullingWhitelist = new HashSet<>(Arrays.asList("minecraft:firework_rocket", "minecraft:boat"));
    public boolean disableF3 = false;
    
}
