package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

public class BlockExplode implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnBlockExplode(BlockExplodeEvent event) {
        for (Block brokenBlock : event.blockList()) {
            Location brokenBlockLocation = brokenBlock.getLocation().toCenterLocation();

            if (NotQuiteModded.GetBlockHandler().BlockTypeFromLocation(brokenBlockLocation) == null)
                continue;
            NQMBlock blockType = NotQuiteModded.GetBlockHandler().BlockTypeFromLocation(brokenBlockLocation);
            if (NotQuiteModded.GetBlockHandler().RemoveDataForBlock(brokenBlockLocation)) {
                blockType.Break(brokenBlockLocation);
                event.setYield(0);
            }
        }
    }
}