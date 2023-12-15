package me.playfulpotato.notquitemodded.block;

import org.apache.commons.lang3.tuple.Pair;
import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class NQMBlock {

    public final Plugin plugin;
    public final String storageKey;
    public final String fullStorageKey;
    public final Boolean doesTick;
    public final Material baseBlockMaterial;
    public int tickRate = 4;
    public boolean hasSupportingEntities = false;
    public Vector[] supportingEntityOffsets;
    public int supportingEntityAmount = 0;
    public HashMap<String, Pair<PersistentDataType, Object>> uniqueDataPairs = new HashMap<>();


    /**
     * The constructor to register important basic data about a block. Not running this inside the constructor is heavily ill-advised. Proceed at your own risk when doing so.
     * @param plugin Represents the plugin that is constructing this block.
     * @param storageKey The storage key that a block uses to save its data. Must be unique to itself. WARNING: Changing this after the fact will lead to old data and prior blocks breaking. Tread carefully.
     * @param baseBlockMaterial The base block material used when running the placement of the block.
     * @param doesTick Whether this block will tick at the tick rate set inside of it.
     * @param supportingEntityCount How many unique entities will accompany the block, this is mainly for visual purposes and advanced uses, this value can be 0. This value must match the amount of supporting entities EXACTLY, otherwise it will throw errors.
     */
    public NQMBlock(@NotNull Plugin plugin, @NotNull String storageKey, @NotNull Material baseBlockMaterial, @NotNull Boolean doesTick, int supportingEntityCount) {
        this.plugin = plugin;
        this.storageKey = storageKey;
        this.baseBlockMaterial = baseBlockMaterial;
        this.doesTick = doesTick;
        this.fullStorageKey = plugin.getName() + ":" + storageKey;
        if (supportingEntityCount > 0) {
            this.hasSupportingEntities = true;
            this.supportingEntityAmount = supportingEntityCount;
            this.supportingEntityOffsets = new Vector[supportingEntityCount];
            for (int index = 0; index < supportingEntityCount; index++) {
                this.supportingEntityOffsets[index] = new Vector(0, 0, 0);
            }
        }
    }
    public <T, Z> void AddNewDataKey(@NotNull String keyName, @NotNull PersistentDataType<T, Z> dataType, @NotNull Z baseValue) {
        uniqueDataPairs.put(keyName, Pair.of(dataType, baseValue));
    }
    public Vector SetSupportingEntityOffset(int index, Vector offset) {
        this.supportingEntityOffsets[index] = offset;
        return offset;
    }
    public void SetCustomKeyValue(@NotNull Location blockLocation, @NotNull String keyString, @NotNull Object setValue) {
        Chunk getterChunk = blockLocation.getChunk();
        PersistentDataContainer chunkStorage = getterChunk.getPersistentDataContainer();

        if (chunkStorage.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + blockLocation.getBlockX() + "/" + blockLocation.getBlockY() + "/" + blockLocation.getBlockZ()), PersistentDataType.INTEGER)) {
            int ID = chunkStorage.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + blockLocation.getBlockX() + "/" + blockLocation.getBlockY() + "/" + blockLocation.getBlockZ()), PersistentDataType.INTEGER);

            Pair<PersistentDataType, Object> dataPair = uniqueDataPairs.get(keyString);
            chunkStorage.set(new NamespacedKey(this.plugin, keyString + "_" + ID), dataPair.getLeft(), setValue);
        }
    }
    public @Nullable Object GetCustomKeyValue(@NotNull Location blockLocation, @NotNull String keyString) {
        Chunk getterChunk = blockLocation.getChunk();
        PersistentDataContainer chunkStorage = getterChunk.getPersistentDataContainer();

        if (chunkStorage.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + blockLocation.getBlockX() + "/" + blockLocation.getBlockY() + "/" + blockLocation.getBlockZ()), PersistentDataType.INTEGER)) {
            int ID = chunkStorage.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + blockLocation.getBlockX() + "/" + blockLocation.getBlockY() + "/" + blockLocation.getBlockZ()), PersistentDataType.INTEGER);

            Pair<PersistentDataType, Object> dataPair = uniqueDataPairs.get(keyString);
            return chunkStorage.get(new NamespacedKey(this.plugin, keyString + "_" + ID), dataPair.getLeft());
        }
        return null;
    }
    public void Place(@NotNull Location placeLocation) {
        placeLocation.toCenterLocation().getBlock().setType(baseBlockMaterial);
        NotQuiteModded.GetBlockHandler().AddDataForNewBlock(placeLocation.toCenterLocation(), this);
        AfterPlace(placeLocation.toCenterLocation());
    }

    public void Break(@NotNull Location breakLocation) { }
    public void Tick(@NotNull Location blockLocation) { }
    public void Interact(@NotNull Location interactLocation, PlayerInteractEvent event) { }
    public void AfterPlace(@NotNull Location placeLocation) { }
    public UUID[] CreateSupportingEntities(@NotNull UUID[] UUIDAllocation, @NotNull Location centerBlockLocation) { return UUIDAllocation; }
}
