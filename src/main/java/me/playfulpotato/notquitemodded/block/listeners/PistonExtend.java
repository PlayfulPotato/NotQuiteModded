package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PistonExtend implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnPistonExtend(BlockPistonExtendEvent event) {
        List<Block> AllBlocks = event.getBlocks();
        for (Block currentCheck : AllBlocks) {
            Location checkLocation = currentCheck.getLocation();
            if (checkLocation.getChunk().getPersistentDataContainer().has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + checkLocation.getBlockX() + "/" + checkLocation.getBlockY() + "/" + checkLocation.getBlockZ()), PersistentDataType.INTEGER)) {
                event.setCancelled(true);
            }
        }
    }
}