package me.playfulpotato.notquitemodded.block;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.utils.LocationHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockHandler {

    public HashMap<UUID, HashMap<Long, List<NQMBlock>>> chunkMap = new HashMap<>();
    public HashMap<String, NQMBlockFactory> factoryMap = new HashMap<>();
    public HashMap<String, HashMap<Long, NQMBlock>> idBlockMap = new HashMap<>();
    public List<NQMBlock> allBlocks = new ArrayList<>();

    public void RegisterFactory(@NotNull NQMBlockFactory factory, int integerCount, int stringCount, int entityCount) {
        factoryMap.put(factory.storageKey, factory);

        factory.intCount = integerCount;
        factory.stringCount = stringCount;
        factory.entityCount = entityCount;
        factory.setStaticDefaults();
        idBlockMap.put(factory.storageKey, new HashMap<>());
        if (factory.doesTick) {
            new BlockTicker(factory);
        }

        NotQuiteModded.blockDatabase.CreateNewBlockTypeTable(factory.storageKey, integerCount, stringCount, entityCount);
    }

    @Nullable
    public NQMBlock getNQMBlock(@NotNull Location checkLocation) {
        HashMap<Long, List<NQMBlock>> chunksMap = chunkMap.get(checkLocation.getWorld().getUID());
        if (chunksMap == null)
            return null;
        List<NQMBlock> blockList = chunksMap.get(checkLocation.getChunk().getChunkKey());
        if (blockList == null)
            return null;
        for (NQMBlock nqmBlock : blockList) {
            if (LocationHelper.blockLocationMatch(checkLocation, nqmBlock.blockLocation)) {
                return nqmBlock;
            }
        }
        return null;
    }

    public void placeBlock(@NotNull Location placeLocation, @NotNull NQMBlockFactory factory) {
        placeLocation = placeLocation.toCenterLocation();
        placeLocation.setYaw(0);
        placeLocation.setPitch(0);
        NQMBlock newBlock = factory.factoryMethod();
        newBlock.blockLocation = placeLocation;
        newBlock.storageKey = factory.storageKey;
        placeLocation.getBlock().setType(factory.blockBaseMaterial);
        newBlock.CreateDefaults(factory.intCount, factory.stringCount, factory.entityCount);
        newBlock.Place();

        NotQuiteModded.blockDatabase.WriteNewBlockData(factory.storageKey, placeLocation.getChunk(), newBlock.integerArray, newBlock.stringArray, newBlock.entityUUIDArray, placeLocation).thenAccept(data -> {
            Integer semanticIDCheck = data.getLeft();
            Long blockIDCheck = data.getRight();
            if (semanticIDCheck != null) {
                newBlock.semanticID = semanticIDCheck;
            }
            if (blockIDCheck != null) {
                newBlock.blockID = blockIDCheck;
            }
            newBlock.InsertIntoMemory();
        });
    }
}
