package me.playfulpotato.notquitemodded.particle;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.projectile.ProjectileHandler;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NQMComplexParticle extends NQMParticle {

    public Display[] displayEntities;
    public float[] yawRotationSpeeds;

    public NQMComplexParticle(int timeLeft, int displayCount, @NotNull Vector velocity, @NotNull Location location) {
        super(timeLeft, velocity, location);
        displayEntities = new Display[displayCount];
        yawRotationSpeeds = new float[displayCount];
        displayEntities = CreateDisplays(displayEntities);
        for (Display display : displayEntities) {
            ForceEntityDefaults(display);
        }
    }

    @Override
    protected void ExtraDestroyLogic() {
        for (int i = 0; i < displayEntities.length; i++) {
            displayEntities[i].remove();
        }
    }

    @Override
    protected void ExtraLogic() {
        for (int i = 0; i < displayEntities.length; i++) {
            Display display = displayEntities[i];
            location.setYaw(display.getYaw() + yawRotationSpeeds[i]);
            location.setPitch(display.getPitch());
            display.teleportAsync(location);
        }
    }

    protected @NotNull Display[] CreateDisplays(@NotNull Display[] displayArray) {
        return displayArray;
    }

    private void ForceEntityDefaults(@Nullable Display display) {
        if (display != null) {
            display.setViewRange(0.5f);
            display.setPersistent(false);
            display.getPersistentDataContainer().set(ProjectileHandler.projectileCollideKey, PersistentDataType.BOOLEAN, true);
            display.setPortalCooldown(1000000);
        }
    }
}
