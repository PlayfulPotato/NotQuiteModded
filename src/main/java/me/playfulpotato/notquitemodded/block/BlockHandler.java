package me.playfulpotato.notquitemodded.block;

import com.google.common.collect.Lists;
import me.playfulpotato.notquitemodded.block.inventory.NQMBlockInventory;
import org.apache.commons.lang3.tuple.Pair;
import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BlockHandler {
    public List<NQMBlock> NQMBlockObjects = new ArrayList<>();
    private final NamespacedKey BlockIDListKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockIDList");

    /**
     * Registers a NQM block class internally into the handler.
     * @return Returns the NQMBlock object. Which you can cast to your specific type if you have special methods or data you wish to invoke or use later on.
     */
    public NQMBlock RegisterNQMBlock(@NotNull NQMBlock nqmBlock) {
        NQMBlockObjects.add(nqmBlock);
        if (nqmBlock.doesTick) {
            new BlockTicker(nqmBlock);
        }
        return nqmBlock;
    }

    public void AddDataForNewBlock(@NotNull Location location, @NotNull NQMBlock blockType) {
        Chunk placeChunk = location.getChunk();

        PersistentDataContainer chunkDataContainer = placeChunk.getPersistentDataContainer();
        PrepareChunkData(placeChunk);
        int ID = AddNextAvailableID(placeChunk);
        chunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING, location.getBlockX() + "/" + location.getBlockY() + "/" + location.getBlockZ());
        chunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + ID), PersistentDataType.STRING, blockType.fullStorageKey);
        chunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + location.getBlockX() + "/" + location.getBlockY() + "/" + location.getBlockZ()), PersistentDataType.INTEGER, ID);
        for (String key : blockType.uniqueDataPairs.keySet()) {
            Pair<PersistentDataType, Object> dataPair = blockType.uniqueDataPairs.get(key);
            chunkDataContainer.set(new NamespacedKey(blockType.plugin, key + "_" + ID), dataPair.getLeft(), dataPair.getRight());
        }
        UUID[] writeEntityUUIDs = blockType.CreateSupportingEntities(new UUID[blockType.supportingEntityAmount], location.clone().toCenterLocation());
        for (int index = 0; index < writeEntityUUIDs.length; index++) {
            UUID currentWriteUUID = writeEntityUUIDs[index];
            chunkDataContainer.set(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + ID + "_" + index), PersistentDataType.STRING, currentWriteUUID.toString());
        }

        if (blockType.doesTick) {
            Optional<BlockTicker> ticker = BlockTicker.AllBlockTickers.stream().filter(currentSearch -> currentSearch.getBlockType() != null && currentSearch.getBlockType().equals(blockType)).findFirst();

            String parseString = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING);
            assert parseString != null;
            String[] locationValues = parseString.split("/");
            Location parsedLocation = new Location(placeChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

            ticker.ifPresent(blockTicker -> blockTicker.AddLocation(parsedLocation));
        }

    }

    public boolean RemoveDataForBlock(@NotNull Location currentLocation) {
        Chunk removeChunk = currentLocation.getChunk();
        PersistentDataContainer chunkDataContainer = removeChunk.getPersistentDataContainer();
        if (chunkDataContainer.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + currentLocation.getBlockX() + "/" + currentLocation.getBlockY() + "/" + currentLocation.getBlockZ()), PersistentDataType.INTEGER)) {
            int ID = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + currentLocation.getBlockX() + "/" + currentLocation.getBlockY() + "/" + currentLocation.getBlockZ()), PersistentDataType.INTEGER);
            NQMBlock blockType = BlockTypeFromStorageKey(Objects.requireNonNull(chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + ID), PersistentDataType.STRING)));

            Location location1 = new Location(currentLocation.getWorld(), currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), 0, 0);
            for (Player syncCheckPlayer : Bukkit.getOnlinePlayers()) {
                InventoryHolder holder = syncCheckPlayer.getOpenInventory().getTopInventory().getHolder(false);
                if (holder == null)
                    continue;
                if (!(holder instanceof NQMBlockInventory))
                    continue;
                NQMBlockInventory nqmInventory = (NQMBlockInventory) holder;
                Location location2 = new Location(nqmInventory.inventoryLocation.getWorld(), nqmInventory.inventoryLocation.getBlockX(), nqmInventory.inventoryLocation.getBlockY(), nqmInventory.inventoryLocation.getBlockZ(), 0, 0);
                if (location1.equals(location2)) {
                    syncCheckPlayer.closeInventory();
                }
            }

            blockType.PreBreak(location1.clone().toCenterLocation());

            for (String key : blockType.uniqueDataPairs.keySet()) {
                chunkDataContainer.remove(new NamespacedKey(blockType.plugin, key + "_" + ID));
            }

            if (blockType.doesTick) {
                Optional<BlockTicker> ticker = BlockTicker.AllBlockTickers.stream().filter(currentSearch -> currentSearch.getBlockType() != null && currentSearch.getBlockType().equals(blockType)).findFirst();

                String parseString = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING);
                assert parseString != null;
                String[] locationValues = parseString.split("/");
                Location parsedLocation = new Location(removeChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

                ticker.ifPresent(blockTicker -> blockTicker.RemoveLocation(parsedLocation));
            }

            chunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID));
            chunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + ID));
            chunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + currentLocation.getBlockX() + "/" + currentLocation.getBlockY() + "/" + currentLocation.getBlockZ()));

            if (blockType.hasSupportingEntities) {
                for (int index = 0; index < blockType.supportingEntityAmount; index++) {
                    String UUIDString = chunkDataContainer.get(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + ID + "_" + index), PersistentDataType.STRING);
                    assert UUIDString != null;
                    Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(UUIDString))).remove();
                    chunkDataContainer.remove(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + ID + "_" + index));
                }
            }

            RevokeCertainID(removeChunk, ID);

            return true;
        }
        return false;
    }

    public NQMBlock BlockTypeFromStorageKey(@NotNull String key) {
        for (NQMBlock nqmBlock : NQMBlockObjects) {
            if (nqmBlock.fullStorageKey.equals(key)) {
                return nqmBlock;
            }
        }
        return null;
    }
    public void MoveBlockLocation(@NotNull Location oldLocation, @NotNull Location newLocation) {
        Chunk oldChunk = oldLocation.getChunk();
        Chunk newChunk = newLocation.getChunk();
        if (oldChunk.equals(newChunk)) {
            // If the location happens in the same chunk there isn't really much we have to do except account for some minor changes.
            // This is part of why this code relies on an ID system to begin with. Ticking and loading the blocks being the other.
            PersistentDataContainer chunkDataContainer = oldChunk.getPersistentDataContainer();

            if (!chunkDataContainer.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + oldLocation.getBlockX() + "/" + oldLocation.getBlockY() + "/" + oldLocation.getBlockZ()), PersistentDataType.INTEGER))
                return;

            int ID = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + oldLocation.getBlockX() + "/" + oldLocation.getBlockY() + "/" + oldLocation.getBlockZ()), PersistentDataType.INTEGER);
            NQMBlock blockType = BlockTypeFromStorageKey(Objects.requireNonNull(chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + ID), PersistentDataType.STRING)));
            for (int index = 0; index < blockType.supportingEntityAmount; index++) {
                UUID entityUUID = UUID.fromString(Objects.requireNonNull(chunkDataContainer.get(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + ID + "_" + index), PersistentDataType.STRING)));
                Entity currentEntity = Bukkit.getEntity(entityUUID);
                assert currentEntity != null;
                currentEntity.teleport(newLocation.toCenterLocation().add(blockType.supportingEntityOffsets[index]));
            }
            chunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + newLocation.getBlockX() + "/" + newLocation.getBlockY() + "/" + newLocation.getBlockZ()), PersistentDataType.INTEGER, ID);
            chunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING, newLocation.getBlockX() + "/" + newLocation.getBlockY() + "/" + newLocation.getBlockZ());
            chunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + oldLocation.getBlockX() + "/" + oldLocation.getBlockY() + "/" + oldLocation.getBlockZ()));

            if (blockType.doesTick) {
                Optional<BlockTicker> ticker = BlockTicker.AllBlockTickers.stream().filter(currentSearch -> currentSearch.getBlockType() != null && currentSearch.getBlockType().equals(blockType)).findFirst();

                String parseString1 = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING);
                assert parseString1 != null;
                String[] locationValues = parseString1.split("/");
                Location parsedLocation1 = new Location(oldChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

                String parseString2 = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING);
                assert parseString2 != null;
                locationValues = parseString2.split("/");
                Location parsedLocation2 = new Location(newChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

                ticker.ifPresent(blockTicker -> blockTicker.RemoveLocation(parsedLocation1));
                ticker.ifPresent(blockTicker -> blockTicker.AddLocation(parsedLocation2));
            }

        } else {
            // Dealing with transferring across chunks is much, much more of a pain. and kinda makes me regret my way of storing information and data.
            PersistentDataContainer oldChunkDataContainer = oldChunk.getPersistentDataContainer();
            PersistentDataContainer newChunkDataContainer = newChunk.getPersistentDataContainer();

            if (!oldChunkDataContainer.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + oldLocation.getBlockX() + "/" + oldLocation.getBlockY() + "/" + oldLocation.getBlockZ()), PersistentDataType.INTEGER))
                return;

            PrepareChunkData(newChunk);

            int oldID = oldChunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + oldLocation.getBlockX() + "/" + oldLocation.getBlockY() + "/" + oldLocation.getBlockZ()), PersistentDataType.INTEGER);
            int ID = AddNextAvailableID(newChunk);
            NQMBlock blockType = BlockTypeFromStorageKey(Objects.requireNonNull(oldChunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + oldID), PersistentDataType.STRING)));

            newChunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING, newLocation.getBlockX() + "/" + newLocation.getBlockY() + "/" + newLocation.getBlockZ());
            newChunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + ID), PersistentDataType.STRING, blockType.fullStorageKey);
            newChunkDataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + newLocation.getBlockX() + "/" + newLocation.getBlockY() + "/" + newLocation.getBlockZ()), PersistentDataType.INTEGER, ID);
            for (String key : blockType.uniqueDataPairs.keySet()) {
                Pair<PersistentDataType, Object> dataPair = blockType.uniqueDataPairs.get(key);
                newChunkDataContainer.set(new NamespacedKey(blockType.plugin, key + "_" + ID), dataPair.getLeft(), Objects.requireNonNull(oldChunkDataContainer.get(new NamespacedKey(blockType.plugin, key + "_" + oldID), dataPair.getLeft())));
                oldChunkDataContainer.remove(new NamespacedKey(blockType.plugin, key + "_" + oldID));
            }
            for (int index = 0; index < blockType.supportingEntityAmount; index++) {
                String oldEntityUUID = Objects.requireNonNull(oldChunkDataContainer.get(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + oldID + "_" + index), PersistentDataType.STRING));
                Entity currentEntity = Bukkit.getEntity(UUID.fromString(oldEntityUUID));
                assert currentEntity != null;
                currentEntity.teleport(newLocation.toCenterLocation().add(blockType.supportingEntityOffsets[index]));
                newChunkDataContainer.set(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + ID + "_" + index), PersistentDataType.STRING, oldEntityUUID);
                oldChunkDataContainer.remove(new NamespacedKey(blockType.plugin, "SupportingEntityUUID_" + oldID + "_" + index));
            }

            if (blockType.doesTick) {
                Optional<BlockTicker> ticker = BlockTicker.AllBlockTickers.stream().filter(currentSearch -> currentSearch.getBlockType() != null && currentSearch.getBlockType().equals(blockType)).findFirst();

                String parseString1 = oldChunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + oldID), PersistentDataType.STRING);
                assert parseString1 != null;
                String[] locationValues = parseString1.split("/");
                Location parsedLocation1 = new Location(oldChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

                String parseString2 = newChunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + ID), PersistentDataType.STRING);
                assert parseString2 != null;
                locationValues = parseString2.split("/");
                Location parsedLocation2 = new Location(newChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

                ticker.ifPresent(blockTicker -> blockTicker.RemoveLocation(parsedLocation1));
                ticker.ifPresent(blockTicker -> blockTicker.AddLocation(parsedLocation2));
            }

            oldChunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + oldID));
            oldChunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + oldID));
            oldChunkDataContainer.remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + oldLocation.getBlockX() + "/" + oldLocation.getBlockY() + "/" + oldLocation.getBlockZ()));

            RevokeCertainID(oldChunk, oldID);
        }
    }
    private void PrepareChunkData(@NotNull Chunk chunk) {
        PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();
        if (!chunkDataContainer.has(BlockIDListKey, PersistentDataType.INTEGER_ARRAY)) {
            chunkDataContainer.set(BlockIDListKey, PersistentDataType.INTEGER_ARRAY, new int[0]);
        }
    }
    private int AddNextAvailableID(@NotNull Chunk chunk) {
        PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();
        int[] listOfCurrentBlockIDs = chunkDataContainer.get(BlockIDListKey, PersistentDataType.INTEGER_ARRAY);
        assert listOfCurrentBlockIDs != null;
        if (listOfCurrentBlockIDs.length == 0) {
            int[] newListOfBlockIDs = new int[1];
            chunkDataContainer.set(BlockIDListKey, PersistentDataType.INTEGER_ARRAY, newListOfBlockIDs);
            return 0;
        }
        for (int indexCheck = 0; indexCheck < listOfCurrentBlockIDs.length; indexCheck++) {
            if (indexCheck != listOfCurrentBlockIDs[indexCheck]) {
                List<Integer> newListOfBlockIDs = Arrays.stream(listOfCurrentBlockIDs).boxed().collect(Collectors.toList());
                newListOfBlockIDs.add(indexCheck, indexCheck);
                int[] arrayOfNewBlockIDs = new int[newListOfBlockIDs.size()];
                for(int i = 0; i < newListOfBlockIDs.size(); i++)
                    arrayOfNewBlockIDs[i] = newListOfBlockIDs.get(i);
                chunkDataContainer.set(BlockIDListKey, PersistentDataType.INTEGER_ARRAY, arrayOfNewBlockIDs);

                return indexCheck;
            }
        }
        List<Integer> newListOfBlockIDs = Arrays.stream(listOfCurrentBlockIDs).boxed().collect(Collectors.toList());
        newListOfBlockIDs.add(newListOfBlockIDs.size());
        int[] arrayOfNewBlockIDs = new int[newListOfBlockIDs.size()];
        for(int i = 0; i < newListOfBlockIDs.size(); i++)
            arrayOfNewBlockIDs[i] = newListOfBlockIDs.get(i);
        chunkDataContainer.set(BlockIDListKey, PersistentDataType.INTEGER_ARRAY, arrayOfNewBlockIDs);
        return (newListOfBlockIDs.size() - 1);
    }

    private void RevokeCertainID(@NotNull Chunk chunk, int IDToRevoke) {
        PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();

        int[] oldListOfBlockIDs = chunkDataContainer.get(BlockIDListKey, PersistentDataType.INTEGER_ARRAY);
        assert oldListOfBlockIDs != null;
        int[] newListOfBlockIDs = new int[oldListOfBlockIDs.length - 1];
        boolean found = false;
        for (int index = 0; index < oldListOfBlockIDs.length; index++) {
            if (IDToRevoke == oldListOfBlockIDs[index]) {
                found = true;
                continue;
            }
            if (found) {
                newListOfBlockIDs[index - 1] = oldListOfBlockIDs[index];
                continue;
            }
            newListOfBlockIDs[index] = oldListOfBlockIDs[index];
        }

        chunkDataContainer.set(BlockIDListKey, PersistentDataType.INTEGER_ARRAY, newListOfBlockIDs);
    }
    public NQMBlock BlockTypeFromLocation(@NotNull Location getLocation) {
        Chunk chunkCheck = getLocation.getChunk();
        PersistentDataContainer chunkDataContainer = chunkCheck.getPersistentDataContainer();
        if (chunkDataContainer.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + getLocation.getBlockX() + "/" + getLocation.getBlockY() + "/" + getLocation.getBlockZ()), PersistentDataType.INTEGER)) {
            int ID = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + getLocation.getBlockX() + "/" + getLocation.getBlockY() + "/" + getLocation.getBlockZ()), PersistentDataType.INTEGER);
            return BlockTypeFromStorageKey(Objects.requireNonNull(chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + ID), PersistentDataType.STRING)));
        }
        return null;
    }
}
