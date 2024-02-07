package me.playfulpotato.notquitemodded.item;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemHandler {
    public HashMap<String, NQMItem> NQMItemRegistry = new HashMap<>();
    public static NamespacedKey itemTypeKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "itemType");

    /**
     * Registers an NQMItem to the internal registry. Required in order to make 99% of anything function.
     * @param itemType The NQMItem object to register.
     * @return Returns the registered item. This lets you edit the item after the fact if you make the object in the parameters.
     */
    public NQMItem RegisterNQMItem(@NotNull NQMItem itemType) {
        NQMItemRegistry.put(itemType.fullStorageKey, itemType);
        return itemType;
    }
    /**
     * Gets the NQM Item object from the registry using the storage key.
     * @param key The full storage key of the item to lookup
     * @return Returns the NQMItem object. If it doesn't exist, returns null.
     */
    public NQMItem ItemTypeFromStorageKey(@NotNull String key) {
        return NQMItemRegistry.get(key);
    }
    /**
     * Gets whether a specific ItemStack is of a custom item. Useful for assured compatibility between packs and less gameplay issues.
     * @param item The item to check if it's custom.
     * @return True if thew item is custom, false if it isn't.
     */
    public boolean ItemIsCustom(@NotNull ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(itemTypeKey);
    }

    /**
     * Returns an NQMItem object correlating to an ItemStack.
     * @param item The item to get the possible NQMItem object of.
     * @return An NQMItem object if the item is really custom, otherwise returns null.
     */
    @Nullable
    public NQMItem itemFromItemStack(@NotNull ItemStack item) {
        return NQMItemRegistry.get(item.getItemMeta().getPersistentDataContainer().get(itemTypeKey, PersistentDataType.STRING));
    }
}
