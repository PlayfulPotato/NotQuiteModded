package me.playfulpotato.notquitemodded.item.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.NQMItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerPlaceBlockItemCheck implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnPlayerPlaceBlock(BlockPlaceEvent event) {

        if (!Objects.requireNonNull(event.getItemInHand()).getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey))
            return;

        // This one singular line is a nightmare and is absolutely dog shit.
        NQMItem itemType = NotQuiteModded.GetItemHandler().ItemTypeFromStorageKey(Objects.requireNonNull(Objects.requireNonNull(event.getItemInHand()).getItemMeta().getPersistentDataContainer().get(ItemHandler.itemTypeKey, PersistentDataType.STRING)));
        itemType.Place(event.getPlayer(), event);
    }
}
