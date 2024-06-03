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
        for (int i = 0; i < event.blockList().size(); i++) {
            Location brokenBlockLocation = event.blockList().get(i).getLocation().toCenterLocation();
            NQMBlock nqmBlock = NotQuiteModded.blockHandler.getNQMBlock(brokenBlockLocation);
            if (nqmBlock == null)
                continue;
            if (nqmBlock.AllowBreak()) {
                nqmBlock.DestroyData();
            }
            event.blockList().remove(i);
        }
    }
}