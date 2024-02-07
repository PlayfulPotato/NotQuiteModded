package me.playfulpotato.notquitemodded.player.instancedata;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

public abstract class NQMInstancedPlayerData {
    private static WeakHashMap<Player, NQMInstancedPlayerData> storageMap = new WeakHashMap<>();

    /**
     * Gets a players instanced storage if it exists, otherwise returns null.
     * @param player The player to get the instanced storage data for.
     * @return Returns the instanced player data. If it hasn't been created, it returns null.
     */
    public static @Nullable NQMInstancedPlayerData GetInstancedStorage(@NotNull Player player) {
        if (storageMap.containsKey(player)) {
            return storageMap.get(player);
        }
        return null;
    }

    /**
     * Allows you to personally remove a storage object.
     * @param player The player to remove the data from.
     */
    public static void RemoveInstancedStorage(@NotNull Player player) {
        storageMap.remove(player);
    }

    /**
     * Creates a new NQMInstancedPlayerData for the player through the struct. Deletes the old one if it exists.
     * @param player The player to create the storage data for.
     */
    public NQMInstancedPlayerData(Player player) {
        storageMap.put(player, this);
    }

}
