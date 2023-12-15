package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.BlockTicker;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class ChunkUnload implements Listener {

    private static final NamespacedKey BlockIDListKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockIDList");

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk unloadedChunk = event.getChunk();
        PersistentDataContainer chunkDataContainer = unloadedChunk.getPersistentDataContainer();

        if (!chunkDataContainer.has(BlockIDListKey, PersistentDataType.INTEGER_ARRAY))
            return;

        int[] unloadingIDs = chunkDataContainer.get(BlockIDListKey, PersistentDataType.INTEGER_ARRAY);
        assert unloadingIDs != null;
        for (int unloadedID : unloadingIDs) {

            NQMBlock blockType = NotQuiteModded.GetBlockHandler().BlockTypeFromStorageKey(Objects.requireNonNull(chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + unloadedID), PersistentDataType.STRING)));

            if (!blockType.doesTick)
                return;

            String parseString = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + unloadedID), PersistentDataType.STRING);
            assert parseString != null;
            String[] locationValues = parseString.split("/");
            Location parsedLocation = new Location(unloadedChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

            Optional<BlockTicker> ticker = BlockTicker.AllBlockTickers.stream().filter(currentSearch -> currentSearch.getBlockType() != null && currentSearch.getBlockType().equals(blockType)).findFirst();
            ticker.ifPresent(blockTicker -> blockTicker.RemoveLocation(parsedLocation.toCenterLocation()));
        }

    }
}