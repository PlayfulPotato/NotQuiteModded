package me.playfulpotato.notquitemodded.player;

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
        Location checkLocation = player.getLocation();

        double minX = Math.min(loc1.x(), loc2.x());
        double maxX = Math.max(loc1.x(), loc2.x());

        double minY = Math.min(loc1.y(), loc2.y());
        double maxY = Math.max(loc1.y(), loc2.y());

        double minZ = Math.min(loc1.z(), loc2.z());
        double maxZ = Math.max(loc1.z(), loc2.z());

        if (loc1.getWorld() != loc2.getWorld()) {
            return false;
        }

        return checkLocation.x() >= minX && checkLocation.x() <= maxX && checkLocation.y() >= minY && checkLocation.y() <= maxY && checkLocation.z() >= minZ && checkLocation.z() <= maxZ;
    }

}
