package me.playfulpotato.notquitemodded.player;

import me.playfulpotato.notquitemodded.utils.LocationHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerHelper {
    /**
     * Check if a player is between two different locations, if the locations are in different worlds, it is always false.
     * @param loc1 The first corner.
     * @param loc2 The second corner.
     * @param player The player to check to see if they are in range.
     * @return Returns true if the player is in the range. Otherwise, returns false.
     */
    public static boolean InLocationalRange(@NotNull Location loc1, @NotNull Location loc2, @NotNull Player player) {
        return LocationHelper.inLocationalRange(loc1, loc2, player.getLocation());
    }

}
