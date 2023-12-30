package me.playfulpotato.notquitemodded.projectile;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProjectileHandler {
    public List<NQMProjectile> activeProjectiles = new ArrayList<>();

    /**
     * Attempts to destroy every projectile in the game.
     */
    public void DestroyAllProjectiles() {
        for (int i = 0; i < activeProjectiles.size(); i++) {
            activeProjectiles.get(i).Kill();
        }
    }

    /**
     * Creates a manually handled projectile.
     * @param projectile The projectile to create.
     * @param projectileOwner The owner of the projectile as an Entity.
     * @param position The location or position the projectile should spawn from.
     * @param velocity The velocity of the projectile, expressed as a vector.
     * @param damage The amount of damage the projectile should be expected to deal.
     * @return The projectile object.
     */
    public NQMProjectile CreateProjectile(@NotNull NQMProjectile projectile, @NotNull Entity projectileOwner, @NotNull Location position, @NotNull Vector velocity, double damage) {
        projectile.damage = damage;
        projectile.position = position.clone();
        projectile.velocity = velocity;
        projectile.projectileOwner = projectileOwner;
        spawnConnectingEntity(projectile);

        activeProjectiles.add(projectile);

        KickstartTicks();
        return projectile;
    }

    /**
     * Creates a projectile with an Entity as the shooter.
     * @param projectile The projectile to shoot.
     * @param projectileOwner The entity owner of the projectile.
     * @param damage The amount of damage the projectile should deal.
     * @return The projectile object.
     */
    public NQMProjectile CreateProjectile(@NotNull NQMProjectile projectile, @NotNull Entity projectileOwner, double damage) {
        projectile.damage = damage;
        projectile.projectileOwner = projectileOwner;
        if (projectileOwner instanceof LivingEntity) {
            projectile.position = ((LivingEntity) projectileOwner).getEyeLocation().clone();
        } else {
            projectile.position = projectileOwner.getLocation().clone().add(0, projectileOwner.getHeight()/2.0, 0);
        }
        projectile.velocity = projectile.position.getDirection().normalize();

        spawnConnectingEntity(projectile);
        if (projectile.hasEntity) {
            projectile.position.add(0, projectile.projectileEntity.getHeight() * -0.5, 0);
            projectile.position.add(projectile.projectileEntity.getLocation().getDirection().clone().normalize());
        }

        activeProjectiles.add(projectile);

        KickstartTicks();
        return projectile;
    }

    private void spawnConnectingEntity(@NotNull NQMProjectile projectile) {
        if (projectile.hasEntity) {
            Entity attemptEntity = projectile.SpawnConnectedentity();
            if (attemptEntity != null) {
                if (attemptEntity instanceof LivingEntity) {
                    ((LivingEntity) attemptEntity).setAI(false);
                    ((LivingEntity) attemptEntity).setCollidable(false);
                }
                attemptEntity.setInvulnerable(true);
                attemptEntity.getPersistentDataContainer().set(new NamespacedKey(NotQuiteModded.GetPlugin(), "DestroyOnChunkLoad"), PersistentDataType.BOOLEAN, true);
                attemptEntity.getPersistentDataContainer().set(new NamespacedKey(NotQuiteModded.GetPlugin(), "NoProjectileCollisions"), PersistentDataType.BOOLEAN, true);
                projectile.projectileEntity = attemptEntity;
            }
        }
    }

    private void KickstartTicks() {
        if (ProjectileTicker.projectileTicker == null) {
            new ProjectileTicker();
        }
    }

}
