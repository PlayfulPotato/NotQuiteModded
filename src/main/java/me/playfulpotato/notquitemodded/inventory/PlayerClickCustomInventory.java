package me.playfulpotato.notquitemodded.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerClickCustomInventory implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void OnInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null)
            return;

        if (inventory.getType() == InventoryType.PLAYER) {
            if (event.isShiftClick()) {
                Inventory topInventory = event.getView().getTopInventory();
                if (topInventory.getHolder() == null)
                    return;

                if (topInventory.getHolder() instanceof NQMInventory) {
                    if (topInventory.firstEmpty() != -1) {
                        event.setCancelled(true);
                        return;
                    }
                    ItemStack clickedItem = event.getCurrentItem();
                    if (clickedItem == null)
                        return;
                    for (ItemStack compareItem : topInventory.getContents()) {
                        if (compareItem == null)
                            continue;
                        if (compareItem.getMaxStackSize() == compareItem.getAmount())
                            continue;
                        if (clickedItem.isSimilar(compareItem)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
            return;
        }

        if (inventory.getHolder() == null)
            return;

        if (inventory.getHolder() instanceof NQMInventory nqmInventory) {
            event.setCancelled(true);
            nqmInventory.InventoryClicked(event);
        }
    }
}
