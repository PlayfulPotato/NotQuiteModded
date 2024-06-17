package me.playfulpotato.notquitemodded.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NQMSimpleProjectile extends NQMProjectile {

    public Entity projectileEntity = null;
    public float yawRotationSpeed = 0;

    public NQMSimpleProjectile(int timeLeft, double hitBoxRadius, double damage, Entity projectileOwner, @NotNull Location position, @NotNull Vector velocity) {
        super(timeLeft, hitBoxRadius, damage, projectileOwner, position, velocity);
        projectileEntity = CreateEntity();
        ForceEntityDefaults(projectileEntity);
    }

    @Override
    protected void ExtraDestroyLogic() {
        if (projectileEntity != null) {
            projectileEntity.remove();
        }
    }

    @Override
    protected void ExtraLogic() {
        if (projectileEntity != null) {
            position.setYaw(projectileEntity.getYaw() + yawRotationSpeed);
            position.setPitch(projectileEntity.getPitch());
            projectileEntity.teleportAsync(position);
        }
    }

    protected @Nullable Entity CreateEntity() {
        return null;
    }

    private void ForceEntityDefaults(@Nullable Entity entity) {
        if (entity != null) {
            entity.setPersistent(false);
            entity.getPersistentDataContainer().set(ProjectileHandler.projectileCollideKey, PersistentDataType.BOOLEAN, true);
            entity.setPortalCooldown(1000000);
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setAI(false);
                livingEntity.setCollidable(false);
            }
            entity.setInvulnerable(true);
        }
    }

}
