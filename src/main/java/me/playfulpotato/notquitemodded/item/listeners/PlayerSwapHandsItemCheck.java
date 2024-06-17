package me.playfulpotato.notquitemodded.item.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.NQMItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerSwapHandsItemCheck implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnPlayerSwapHands(PlayerSwapHandItemsEvent event) {

        if (event.getOffHandItem().getItemMeta() == null)
            return;

        if (event.getOffHandItem().getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey))
            return;

        String itemStorageKey = event.getOffHandItem().getItemMeta().getPersistentDataContainer().get(ItemHandler.itemTypeKey, PersistentDataType.STRING);
        if (itemStorageKey == null)
            return;

        NQMItem itemType = NotQuiteModded.GetItemHandler().ItemTypeFromStorageKey(itemStorageKey);
        itemType.Swap(event.getPlayer(), event);
    }
}