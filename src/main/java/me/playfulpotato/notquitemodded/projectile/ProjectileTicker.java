package me.playfulpotato.notquitemodded.projectile;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.scheduler.BukkitRunnable;

public class ProjectileTicker extends BukkitRunnable {

    public static ProjectileTicker projectileTicker = null;

    public ProjectileTicker() {
        if (projectileTicker != null)
            return;

        projectileTicker = this;
        runTaskTimer(NotQuiteModded.GetPlugin(), 1, 1);
    }

    @Override
    public void run() {
        for (int i = 0; i < NotQuiteModded.projectileHandler.activeProjectiles.size(); i++) {
            NotQuiteModded.projectileHandler.activeProjectiles.get(i).LogicTick();
        }
    }

}
