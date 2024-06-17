package me.playfulpotato.notquitemodded.projectile;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class ProjectileHandler {
    public List<NQMProjectile> activeProjectiles = new ArrayList<>();
    public static final NamespacedKey projectileCollideKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "NoProjectileCollisions");

    public ProjectileHandler() {
        new ProjectileTicker();
    }

    public void DestroyAllProjectiles() {
        for (int i = 0; i < activeProjectiles.size(); i++) {
            activeProjectiles.get(i).Destroy();
        }
    }

}
