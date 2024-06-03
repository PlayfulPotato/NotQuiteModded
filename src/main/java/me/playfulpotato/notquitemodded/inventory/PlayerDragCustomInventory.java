package me.playfulpotato.notquitemodded.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class PlayerDragCustomInventory implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void OnInventoryClick(InventoryDragEvent event) {

        if (event.getView().getType() == InventoryType.PLAYER)
            return;

        Inventory checkInventory = event.getView().getTopInventory();
        if (checkInventory.getHolder() == null)
            return;
        if (checkInventory.getHolder() instanceof NQMInventory nqmInventory) {
            for (int rawSlot : event.getRawSlots()) {
                checkInventory = event.getView().getInventory(rawSlot);
                if (checkInventory == null)
                    continue;
                if (checkInventory.getHolder() == null)
                    continue;
                if (checkInventory.getHolder() instanceof NQMInventory) {
                    event.setCancelled(true);
                    nqmInventory.InventoryDragged(event);
                    return;
                }
            }
        }
    }
}