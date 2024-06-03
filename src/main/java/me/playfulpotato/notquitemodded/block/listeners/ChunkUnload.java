package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class ChunkUnload implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk unloadedChunk = event.getChunk();

        if (NotQuiteModded.blockHandler.chunkMap.get(unloadedChunk.getWorld().getUID()) == null)
            return;

        List<NQMBlock> blockList = NotQuiteModded.blockHandler.chunkMap.get(unloadedChunk.getWorld().getUID()).get(unloadedChunk.getChunkKey());

        if (blockList == null)
            return;

        for (int i = 0; i < blockList.size(); i++) {
            NQMBlock nqmBlock = blockList.get(i);
            nqmBlock.SaveData();
            nqmBlock.UnloadData();
        }
    }
}