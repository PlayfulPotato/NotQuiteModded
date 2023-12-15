package me.playfulpotato.notquitemodded.item.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.NQMItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerPrepareCraftItemCheck implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void OnPlayerPrepareCraftEvent(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        for (ItemStack itemStack : inventory.getMatrix()) {
            if (itemStack == null)
                continue;

            if (itemStack.getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey)) {
                NQMItem itemType = NotQuiteModded.GetItemHandler().ItemTypeFromStorageKey(Objects.requireNonNull(Objects.requireNonNull(itemStack).getItemMeta().getPersistentDataContainer().get(ItemHandler.itemTypeKey, PersistentDataType.STRING)));
                if (itemType.DisableMostCraftingUse) {
                    inventory.setResult(null);
                    return;
                }
            }
        }
    }
}