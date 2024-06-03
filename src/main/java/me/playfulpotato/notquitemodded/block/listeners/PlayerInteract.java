package me.playfulpotato.notquitemodded.block.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.BlockDatabase;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerInteract implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnPlayerInteract(PlayerInteractEvent event) {

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getPlayer().isSneaking() || Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND))
            return;

        if (event.getClickedBlock() == null)
            return;

        Location interactedBlockLocation = event.getClickedBlock().getLocation().toCenterLocation();
        NQMBlock nqmBlock = NotQuiteModded.blockHandler.getNQMBlock(interactedBlockLocation);
        if (nqmBlock == null)
            return;
        nqmBlock.Interact(event);
    }
}