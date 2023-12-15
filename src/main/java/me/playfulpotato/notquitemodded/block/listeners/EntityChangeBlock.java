package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlock implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnEntityChangeBlock(EntityChangeBlockEvent event) {
        Location blockLocation = event.getBlock().getLocation().toCenterLocation();

        if (NotQuiteModded.GetBlockHandler().BlockTypeFromLocation(blockLocation) == null)
            return;
        event.setCancelled(true);
    }
}