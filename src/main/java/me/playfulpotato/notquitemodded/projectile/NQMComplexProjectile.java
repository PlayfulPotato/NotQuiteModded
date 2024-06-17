package me.playfulpotato.notquitemodded.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NQMComplexProjectile extends NQMProjectile {

    public Entity[] projectileEntities;
    public float[] yawRotationSpeeds;

    public NQMComplexProjectile(int timeLeft, double hitBoxRadius, double damage, Entity projectileOwner, @NotNull Location position, @NotNull Vector velocity, int entityCount) {
        super(timeLeft, hitBoxRadius, damage, projectileOwner, position, velocity);
        projectileEntities = new Entity[entityCount];
        yawRotationSpeeds = new float[entityCount];
        projectileEntities = CreateEntities(projectileEntities);
        for (Entity entity : projectileEntities) {
            ForceEntityDefaults(entity);
        }
    }

    @Override
    protected void ExtraDestroyLogic() {
        for (int i = 0; i < projectileEntities.length; i++) {
            projectileEntities[i].remove();
        }
    }

    @Override
    protected void ExtraLogic() {
        for (int i = 0; i < projectileEntities.length; i++) {
            Entity entity = projectileEntities[i];
            position.setYaw(entity.getYaw() + yawRotationSpeeds[i]);
            position.setPitch(entity.getPitch());
            entity.teleportAsync(position);
        }
    }

    protected @NotNull Entity[] CreateEntities(@NotNull Entity[] entityArray) {
        return entityArray;
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