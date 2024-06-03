package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
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
        for (Block currentCheck : event.getBlocks()) {
            Location checkLocation = currentCheck.getLocation().toCenterLocation();
            NQMBlock nqmBlock = NotQuiteModded.blockHandler.getNQMBlock(checkLocation);
            if (nqmBlock == null)
                continue;
            event.setCancelled(true);
        }
    }
}