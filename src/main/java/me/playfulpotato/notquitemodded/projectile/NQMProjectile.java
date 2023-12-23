package me.playfulpotato.notquitemodded.projectile;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class NQMProjectile {
    public Entity projectileEntity;
    public Vector velocity;
    public Location position;
    public Entity projectileOwner;
    public boolean collideWithBlocks = true;
    public boolean collideWithEntities = true;
    public boolean damageEntities = true;
    public final boolean hasEntity;
    public int timeLeft;
    public double hitBoxRadius;
    public double damage;
    public int pierce = 0;
    private final NamespacedKey projectileCollideKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "NoProjectileCollisions");

    public NQMProjectile(boolean hasEntity, int timeLeft, double hitBoxRadius) {
        this.hasEntity = hasEntity;
        this.timeLeft = timeLeft;
        this.hitBoxRadius = hitBoxRadius;
    }

    void LogicTick() {
        timeLeft--;
        if (timeLeft <= 0) {
            Kill();
            return;
        }
        if (collideWithBlocks) {
            Location checkLocation = position.clone().add(velocity);
            double distance = checkLocation.distance(position);

            RayTraceResult rayResult = checkLocation.getWorld().rayTraceBlocks(position, velocity.clone().normalize(), distance, FluidCollisionMode.NEVER, true);
            if (rayResult != null) {
                Block block = rayResult.getHitBlock();
                if (block != null) {
                    Location hitLocation = rayResult.getHitPosition().toLocation(checkLocation.getWorld());
                    position = hitLocation.clone().setDirection(checkLocation.getDirection());
                    if (blockCollision(position, rayResult.getHitBlockFace())) {
                        Kill();
                        return;
                    }
                } else {
                    position = checkLocation;
                }
            } else {
                position = checkLocation;
            }
        } else {
            position.add(velocity);
        }
        if (collideWithEntities) {
            Object[] nearbyLivingEntities = position.getNearbyLivingEntities(hitBoxRadius).toArray();
            for (int index = 0; index < nearbyLivingEntities.length; index++) {
                LivingEntity currentEntity = (LivingEntity) nearbyLivingEntities[index];

                if (currentEntity.isInvulnerable())
                    continue;
                if (currentEntity.equals(projectileOwner) || currentEntity.equals(projectileEntity))
                    continue;
                if (currentEntity.getPersistentDataContainer().has(projectileCollideKey))
                    continue;

                if (damageEntities)
                    currentEntity.damage(damage);

                hitEntity(currentEntity);

                pierce--;
                if (pierce == -1) {
                    Kill();
                    return;
                }
            }
        }
        if (hasEntity) {
            projectileEntity.teleport(position);
            projectileEntity.setPortalCooldown(20);
        }

        Tick();
    }
    void Kill() {
        if (hasEntity)
            projectileEntity.remove();

        KillEffects();

        NotQuiteModded.projectileHandler.activeProjectiles.remove(this);
        if (NotQuiteModded.projectileHandler.activeProjectiles.size() == 0 && ProjectileTicker.projectileTicker != null) {
            ProjectileTicker.projectileTicker.cancel();
            ProjectileTicker.projectileTicker = null;
        }
    }

    public @Nullable Entity SpawnConnectedentity() {
        return null;
    }

    public boolean blockCollision(@NotNull Location hitLocation, @Nullable BlockFace hitFace) {
        return true;
    }
    public void Tick() {}
    public void KillEffects() { }
    public void hitEntity(LivingEntity hitEntity) { }
}
