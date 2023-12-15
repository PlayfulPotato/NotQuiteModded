package me.playfulpotato.notquitemodded.projectile;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ProjectileTicker extends BukkitRunnable {

    public static ProjectileTicker projectileTicker = null;

    public ProjectileTicker() {
        if (projectileTicker != null)
            return;
        runTaskTimer(NotQuiteModded.GetPlugin(), 0, 1);
        projectileTicker = this;
    }

    @Override
    public void run() {
        List<NQMProjectile> activeProjectiles = NotQuiteModded.projectileHandler.activeProjectiles;
        for (int index = 0; index < activeProjectiles.size(); index++) {
            activeProjectiles.get(index).LogicTick();
        }
    }

}
