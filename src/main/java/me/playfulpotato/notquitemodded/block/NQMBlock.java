package me.playfulpotato.notquitemodded.block;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.inventory.NQMBlockInventory;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class NQMBlock {

    public int[] integerArray;
    public String[] stringArray;
    public UUID[] entityUUIDArray;
    protected Entity[] entityArray;
    public Location blockLocation;
    public String storageKey;
    public long blockID;
    public int semanticID;

    public void CreateDefaults(int intCount, int stringCount, int entityCount) {
        integerArray = new int[intCount];
        stringArray = new String[stringCount];
        entityUUIDArray = CreateEntities(new UUID[entityCount]);
        InitializeEntityArray();
        SetDefaultSaveData();
    }

    public CompletableFuture<Boolean> SaveData() {
        return NotQuiteModded.blockDatabase.WriteUniqueBlockInformation(storageKey, blockID, integerArray, stringArray, entityUUIDArray);
    }

    public void LoadData(int intCount, int stringCount, int entityCount) {
        NotQuiteModded.blockDatabase.ObtainAllUniqueInformationAboutBlock(storageKey, blockID, intCount, stringCount, entityCount).thenAccept(allData -> {
            integerArray = allData.getLeft().getLeft();
            stringArray = allData.getLeft().getRight();
            entityUUIDArray = allData.getRight();
            InitializeEntityArray();
            PostLoad();
        });
    }
    public void InitializeEntityArray() {
        entityArray = new Entity[entityUUIDArray.length];
        for (int i = 0; i < entityUUIDArray.length; i++) {
            entityArray[i] = blockLocation.getWorld().getEntity(entityUUIDArray[i]);
        }
    }
    public void DestroyData() {
        blockLocation.getBlock().setType(Material.AIR);
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder(false);
            if (holder == null)
                continue;
            if (!(holder instanceof NQMBlockInventory nqmBlockInventory))
                continue;
            if (nqmBlockInventory.block.equals(this)) {
                player.closeInventory();
            }
        }
        UnloadData();
        for (Entity entity : entityArray) {
            entity.remove();
        }
        NotQuiteModded.blockDatabase.DeleteBlockData(storageKey, blockID, semanticID, blockLocation.getChunk());
        Break();
    }
    public void UnloadData() {
        NotQuiteModded.blockHandler.idBlockMap.get(storageKey).remove(blockID);
        HashMap<Long, List<NQMBlock>> worldMap = NotQuiteModded.blockHandler.chunkMap.get(blockLocation.getWorld().getUID());
        List<NQMBlock> blockList = worldMap.get(blockLocation.getChunk().getChunkKey());
        blockList.remove(this);
        if (blockList.isEmpty()) {
            worldMap.remove(blockLocation.getChunk().getChunkKey());
        }
        NotQuiteModded.blockHandler.allBlocks.remove(this);
    }
    public void InsertIntoMemory() {
        HashMap<Long, NQMBlock> idMap = NotQuiteModded.blockHandler.idBlockMap.get(storageKey);
        if (idMap == null) {
            NotQuiteModded.blockHandler.idBlockMap.put(storageKey, new HashMap<>());
            idMap = NotQuiteModded.blockHandler.idBlockMap.get(storageKey);
        }
        idMap.put(blockID, this);
        HashMap<Long, List<NQMBlock>> worldMap = NotQuiteModded.blockHandler.chunkMap.get(blockLocation.getWorld().getUID());
        if (worldMap == null) {
            NotQuiteModded.blockHandler.chunkMap.put(blockLocation.getWorld().getUID(), new HashMap<>());
            worldMap = NotQuiteModded.blockHandler.chunkMap.get(blockLocation.getWorld().getUID());
        }
        List<NQMBlock> blockList = worldMap.get(blockLocation.getChunk().getChunkKey());
        if (blockList == null) {
            NotQuiteModded.blockHandler.chunkMap.get(blockLocation.getWorld().getUID()).put(blockLocation.getChunk().getChunkKey(), new ArrayList<>());
            blockList = worldMap.get(blockLocation.getChunk().getChunkKey());
        }
        for (int i = 0; i < blockList.size(); i++) {
            NQMBlock nqmBlock = blockList.get(i);
            if (nqmBlock.blockID == blockID && Objects.equals(nqmBlock.storageKey, storageKey)) {
                blockList.remove(i);
            }
        }
        blockList.add(this);
        for (int i = 0; i < NotQuiteModded.blockHandler.allBlocks.size(); i++) {
            NQMBlock nqmBlock = NotQuiteModded.blockHandler.allBlocks.get(i);
            if (nqmBlock.blockID == blockID && Objects.equals(nqmBlock.storageKey, storageKey)) {
                NotQuiteModded.blockHandler.allBlocks.remove(i);
            }
        }
        NotQuiteModded.blockHandler.allBlocks.add(this);
    }

    public void SetDefaultSaveData() { }
    public void Break() { }
    public boolean AllowBreak() { return true; }
    public void Tick() { }
    public void Interact(PlayerInteractEvent event) { }
    public void Place() { }
    public UUID[] CreateEntities(@NotNull UUID[] UUIDAllocation) { return UUIDAllocation; }
    public void PostLoad() { }

}
