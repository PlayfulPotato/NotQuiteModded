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
    public final String storageKey;
    public final Plugin plugin;
    public final String storageName;

    public NQMBlockFactory(Plugin plugin, String storage) {
        this.plugin = plugin;
        this.storageName = storage;
        storageKey = plugin.getName() + ":" + storage;
    }

    public abstract NQMBlock factoryMethod();
    public void setStaticDefaults() { };
}
