package me.playfulpotato.notquitemodded.block.inventory;

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

        if (event.getView().getTopInventory().getHolder() instanceof NQMBlockInventory) {
            NQMBlockInventory blockInventory = (NQMBlockInventory) event.getView().getTopInventory().getHolder();
            assert blockInventory != null;
            blockInventory.InventoryDragged(event);
        }
    }
}