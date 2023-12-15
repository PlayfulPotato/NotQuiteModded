package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.BlockTicker;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import me.playfulpotato.notquitemodded.cleanup.ChunkLoadCleanup;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class ChunkLoad implements Listener {

    private static final NamespacedKey BlockIDListKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockIDList");

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk())
            return;

        Chunk loadedChunk = event.getChunk();

        ChunkLoadCleanup.CleanupChunk(loadedChunk);

        PersistentDataContainer chunkDataContainer = loadedChunk.getPersistentDataContainer();

        if (!chunkDataContainer.has(BlockIDListKey, PersistentDataType.INTEGER_ARRAY))
            return;

        int[] LoadingIDs = chunkDataContainer.get(BlockIDListKey, PersistentDataType.INTEGER_ARRAY);
        assert LoadingIDs != null;
        for (int LoadedID : LoadingIDs) {

            NQMBlock blockType = NotQuiteModded.GetBlockHandler().BlockTypeFromStorageKey(Objects.requireNonNull(chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockType_" + LoadedID), PersistentDataType.STRING)));

            if (!blockType.doesTick)
                return;

            String parseString = chunkDataContainer.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "BlockLocation_" + LoadedID), PersistentDataType.STRING);
            assert parseString != null;
            String[] locationValues = parseString.split("/");
            Location parsedLocation = new Location(loadedChunk.getWorld(), Integer.parseInt(locationValues[0]) + 0.5, Integer.parseInt(locationValues[1]) + 0.5, Integer.parseInt(locationValues[2]) + 0.5);

            Optional<BlockTicker> ticker = BlockTicker.AllBlockTickers.stream().filter(currentSearch -> currentSearch.getBlockType() != null && currentSearch.getBlockType().equals(blockType)).findFirst();
            ticker.ifPresent(blockTicker -> blockTicker.AddLocation(parsedLocation.toCenterLocation()));
        }

    }
}
