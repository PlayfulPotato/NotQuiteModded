package me.playfulpotato.notquitemodded.block;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class NQMBlockFactory {

    public int entityCount;
    public int stringCount;
    public int intCount;
    public Material blockBaseMaterial = Material.STONE;
    public int tickRate = 4;
    public boolean doesTick = false;
    public String storageKey;

    public NQMBlockFactory(Plugin plugin, String storage) {
        storageKey = plugin.getName() + ":" + storage;
    }

    public abstract NQMBlock factoryMethod();
    public void setStaticDefaults() { };
}
