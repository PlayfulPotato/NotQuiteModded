package me.playfulpotato.notquitemodded.recipe.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.NQMItem;
import me.playfulpotato.notquitemodded.recipe.NQMShapedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PrepareCraftCheckRecipe implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void OnPlayerPrepareCraftEvent(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        int itemCount = 0;
        int customItemCount = 0;

        if (inventory.getMatrix().length < 6) {
            return;
        }
        for (ItemStack itemStack : inventory.getMatrix()) {
            if (itemStack == null)
                continue;
            itemCount++;
            if (itemStack.getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey))
                customItemCount++;
        }
        if (customItemCount == 0)
            return;

        for (NQMShapedRecipe shapedRecipe : NotQuiteModded.GetRecipeHandler().shapedRecipes) {
            if (shapedRecipe.itemCount != itemCount)
                continue;
            if (shapedRecipe.customItemCount != customItemCount)
                continue;

            int index = 0;
            int goodItems = 0;
            boolean selfSimilar = true;
            for (ItemStack itemStack : inventory.getMatrix()) {
                if (itemStack == null || shapedRecipe.gridSetup[index] == null) {
                    index++;
                    continue;
                }
                if (itemStack.getType() == shapedRecipe.gridSetup[index]) {
                    if (itemStack.getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey)) {
                        if (shapedRecipe.specialIDs[index] == null) {
                            index++;
                            continue;
                        }

                        String storageKey = itemStack.getItemMeta().getPersistentDataContainer().get(ItemHandler.itemTypeKey, PersistentDataType.STRING);
                        if (Objects.equals(storageKey, shapedRecipe.specialIDs[index])) {
                            goodItems++;
                            assert storageKey != null;
                            if (!NotQuiteModded.itemHandler.ItemTypeFromStorageKey(storageKey).baseItemStack.isSimilar(itemStack)) {
                                selfSimilar = false;
                            }
                        }
                    } else {
                        goodItems++;
                    }
                }
                index++;
            }
            if (goodItems == shapedRecipe.itemCount) {
                if (selfSimilar) {
                    inventory.setResult(shapedRecipe.resultItem);
                    return;
                }

                ItemStack resultingItem = shapedRecipe.resultItem.clone();
                ItemMeta metaAddition = resultingItem.getItemMeta();
                metaAddition.getPersistentDataContainer().set(new NamespacedKey(NotQuiteModded.GetPlugin(), "CraftedSpecial"), PersistentDataType.BOOLEAN, true);
                resultingItem.setItemMeta(metaAddition);
                inventory.setResult(resultingItem);
                return;
            }
        }
    }
}