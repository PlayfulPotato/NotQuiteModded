package me.playfulpotato.notquitemodded.item;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemHandler {
    public List<NQMItem> NQMItemObjects = new ArrayList<>();
    public static NamespacedKey itemTypeKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "itemType");
    public NQMItem RegisterNQMItem(@NotNull NQMItem itemType) {
        NQMItemObjects.add(itemType);
        return itemType;
    }

    public NQMItem ItemTypeFromStorageKey(@NotNull String key) {
        for (NQMItem nqmItem : NQMItemObjects) {
            if (nqmItem.fullStorageKey.equals(key)) {
                return nqmItem;
            }
        }
        return null;
    }
}
