package me.playfulpotato.notquitemodded.block.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PlayerClickCustomInventory implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void OnInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null)
            return;

        if (inventory.getHolder(false) == null)
            return;

        if (inventory.getHolder(false) instanceof NQMBlockInventory) {
            NQMBlockInventory blockInventory = (NQMBlockInventory) inventory.getHolder();
            assert blockInventory != null;
            blockInventory.InventoryClicked(event);
        }
    }
}
