package me.playfulpotato.notquitemodded.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationHelper {

    public static boolean inLocationalRange(@NotNull Location loc1, @NotNull Location loc2, @NotNull Location checkLocation) {
        double minX = Math.min(loc1.x(), loc2.x());
        double maxX = Math.max(loc1.x(), loc2.x());

        double minY = Math.min(loc1.y(), loc2.y());
        double maxY = Math.max(loc1.y(), loc2.y());

        double minZ = Math.min(loc1.z(), loc2.z());
        double maxZ = Math.max(loc1.z(), loc2.z());

        if (loc1.getWorld().getUID() != loc2.getWorld().getUID()) {
            return false;
        }

        return checkLocation.x() >= minX && checkLocation.x() <= maxX && checkLocation.y() >= minY && checkLocation.y() <= maxY && checkLocation.z() >= minZ && checkLocation.z() <= maxZ;
    }

    public static boolean blockLocationMatch(@NotNull Location loc1, @NotNull Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }
}
