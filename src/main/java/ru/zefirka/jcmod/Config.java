package ru.zefirka.jcmod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Config {

    public int configVersion = 5;
    public boolean renderNametagsThroughWalls = true;
    public Set<String> blockEntityWhitelist = new HashSet<>(Arrays.asList("minecraft:beacon", "create:rope_pulley", "create:hose_pulley", "betterend:eternal_pedestal"));
    public Set<String> entityWhitelist = new HashSet<>(Arrays.asList("botania:mana_burst"));
    public int tracingDistance = 70;
    public boolean debugMode = false;
    public int sleepDelay = 10;
    public int hitboxLimit = 50;
    public boolean skipMarkerArmorStands = true;
    public boolean tickCulling = true;
    public Set<String> tickCullingWhitelist = new HashSet<>(Arrays.asList("minecraft:firework_rocket", "minecraft:boat"));
    public boolean disableF3 = false;
    
}
