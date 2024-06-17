package me.playfulpotato.notquitemodded.projectile;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NQMProjectile {
    public Vector velocity;
    public Location position;
    public Entity projectileOwner;
    public boolean collideWithBlocks = true;
    public boolean collideWithEntities = true;
    public boolean damageEntities = true;
    public int timeLeft;
    public double hitBoxRadius;
    public double damage;
    public int pierce = 0;

    public NQMProjectile(int timeLeft, double hitBoxRadius, double damage, Entity projectileOwner, @NotNull Location position, @NotNull Vector velocity) {
        this.timeLeft = timeLeft;
        this.hitBoxRadius = hitBoxRadius;
        this.damage = damage;
        this.projectileOwner = projectileOwner;
        this.position = position;
        this.velocity = velocity;

        NotQuiteModded.projectileHandler.activeProjectiles.add(this);
    }

    void LogicTick() {
        timeLeft--;
        if (timeLeft <= 0) {
            Destroy();
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
                        Destroy();
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
                if (currentEntity.equals(projectileOwner))
                    continue;
                if (currentEntity.getPersistentDataContainer().has(ProjectileHandler.projectileCollideKey))
                    continue;

                if (damageEntities)
                    currentEntity.damage(damage, projectileOwner);

                hitEntity(currentEntity);

                pierce--;
                if (pierce == -1) {
                    Destroy();
                    return;
                }
            }
        }

        ExtraLogic();
        Tick();
    }
    void Destroy() {
        NotQuiteModded.projectileHandler.activeProjectiles.remove(this);

        ExtraDestroyLogic();
        DestroyEffects();
    }

    public boolean blockCollision(@NotNull Location hitLocation, @Nullable BlockFace hitFace) {
        return true;
    }

    protected void ExtraLogic() {}
    public void Tick() {}

    protected void ExtraDestroyLogic() {}
    public void DestroyEffects() { }
    public void hitEntity(LivingEntity hitEntity) { }
}
