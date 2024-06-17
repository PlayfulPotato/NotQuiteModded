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

public abstract class NQMSimpleParticle extends NQMParticle {

    public Display displayEntity = null;
    public float yawRotationSpeed = 0;

    public NQMSimpleParticle(int timeLeft, @NotNull Vector velocity, @NotNull Location location) {
        super(timeLeft, velocity, location);
        displayEntity = CreateDisplay();
        ForceEntityDefaults(displayEntity);
    }

    @Override
    protected void ExtraDestroyLogic() {
        if (displayEntity != null) {
            displayEntity.remove();
        }
    }

    @Override
    protected void ExtraLogic() {
        if (displayEntity != null) {
            location.setYaw(displayEntity.getYaw() + yawRotationSpeed);
            location.setPitch(displayEntity.getPitch());
            displayEntity.teleportAsync(location);
        }
    }

    protected @Nullable Display CreateDisplay() {
        return null;
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
