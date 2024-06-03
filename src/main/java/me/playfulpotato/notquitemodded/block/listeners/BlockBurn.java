package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class BlockBurn implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnBlockBurn(BlockBurnEvent event) {
        Location brokenBlockLocation = event.getBlock().getLocation().toCenterLocation();
        NQMBlock nqmBlock = NotQuiteModded.blockHandler.getNQMBlock(brokenBlockLocation);
        if (nqmBlock == null)
            return;
        if (nqmBlock.AllowBreak()) {
            nqmBlock.DestroyData();
        } else {
            event.setCancelled(true);
        }
    }
}