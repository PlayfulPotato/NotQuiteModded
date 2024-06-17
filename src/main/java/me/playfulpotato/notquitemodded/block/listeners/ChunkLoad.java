package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import me.playfulpotato.notquitemodded.block.NQMBlockFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.List;

public class ChunkLoad implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk())
            return;

        Chunk loadedChunk = event.getChunk();

        if (!NotQuiteModded.blockDatabase.TableExistsOffChunk(loadedChunk).join())
            return;

        List<Pair<String, Pair<int[], Long>>> blockData = NotQuiteModded.blockDatabase.RetrieveAllBlockDataInChunk(loadedChunk).join();
        for (Pair<String, Pair<int[], Long>> currentData : blockData) {
            NQMBlockFactory factory = NotQuiteModded.blockHandler.factoryMap.get(currentData.getLeft());
            NQMBlock loadedBlock = factory.factoryMethod();
            loadedBlock.blockID = currentData.getRight().getRight();
            loadedBlock.storageKey = factory.storageKey;
            int[] integerData = currentData.getRight().getLeft();
            loadedBlock.blockLocation = new Location(loadedChunk.getWorld(), integerData[0], integerData[1], integerData[2]).toCenterLocation();
            loadedBlock.blockLocation.setPitch(0);
            loadedBlock.blockLocation.setYaw(0);
            loadedBlock.semanticID = integerData[3];
            loadedBlock.LoadData(factory.intCount, factory.stringCount, factory.entityCount);
            loadedBlock.InsertIntoMemory();
        }
    }
}
