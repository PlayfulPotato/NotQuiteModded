package me.playfulpotato.notquitemodded.recipe.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CheckForSpecialCraftClick implements Listener {

    @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void OnPlayerPrepareCraftEvent(InventoryClickEvent event) {
        Inventory checkInventory = event.getClickedInventory();
        if (checkInventory == null)
            return;
        if (checkInventory instanceof CraftingInventory) {
            if (event.getSlotType() != InventoryType.SlotType.RESULT)
                return;

            CraftingInventory inventory = (CraftingInventory) checkInventory;

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null)
                return;
            if (clickedItem.getItemMeta() == null)
                return;
            if (!clickedItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(NotQuiteModded.GetPlugin(), "CraftedSpecial")))
                return;

            if (event.isShiftClick()) {
                //TODO: Actually make shift clicking items in recipes involving renamed/non exact items work.

                event.setCancelled(true);
                return;
            } else {
                ItemStack stackOnCursor = event.getCursor();
                int amount = 1;
                if (stackOnCursor.getAmount() > 0) {
                    ItemStack checkStack = clickedItem.clone();
                    ItemMeta meta = checkStack.getItemMeta();
                    meta.getPersistentDataContainer().remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "CraftedSpecial"));
                    checkStack.setItemMeta(meta);
                    if (!stackOnCursor.isSimilar(checkStack) || stackOnCursor.getMaxStackSize() < (stackOnCursor.getAmount() + checkStack.getAmount())) {
                        event.setCancelled(true);
                        return;
                    }
                    amount += stackOnCursor.getAmount();
                }
                ItemStack[] newMatrix = inventory.getMatrix();
                for (int index = 0; index < newMatrix.length; index++) {
                    ItemStack currentEdit = newMatrix[index];
                    if (currentEdit == null)
                        continue;
                    if (currentEdit.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(NotQuiteModded.GetPlugin(), "CraftedSpecial"))) {
                        continue;
                    }
                    if (currentEdit.getAmount() > 1) {
                        currentEdit.setAmount(currentEdit.getAmount() - 1);
                    } else {
                        newMatrix[index] = null;
                        continue;
                    }
                    newMatrix[index] = currentEdit;
                }
                inventory.setMatrix(newMatrix);

                ItemMeta meta = clickedItem.getItemMeta();
                meta.getPersistentDataContainer().remove(new NamespacedKey(NotQuiteModded.GetPlugin(), "CraftedSpecial"));
                clickedItem.setItemMeta(meta);
                clickedItem.setAmount(amount);
                // Had to use a piece of deprecated API ew
                event.setCursor(clickedItem);
            }
        }
    }
}