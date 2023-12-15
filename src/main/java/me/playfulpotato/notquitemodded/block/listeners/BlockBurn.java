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

        if (NotQuiteModded.GetBlockHandler().BlockTypeFromLocation(brokenBlockLocation) == null)
            return;
        NQMBlock blockType = NotQuiteModded.GetBlockHandler().BlockTypeFromLocation(brokenBlockLocation);
        if (NotQuiteModded.GetBlockHandler().RemoveDataForBlock(brokenBlockLocation)) {
            blockType.Break(brokenBlockLocation);
        }
    }
}