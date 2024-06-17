package me.playfulpotato.notquitemodded.particle;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTicker extends BukkitRunnable {

    public static ParticleTicker particleTicker = null;

    public ParticleTicker() {
        if (particleTicker != null)
            return;

        particleTicker = this;
        runTaskTimer(NotQuiteModded.GetPlugin(), 1, 1);
    }

    @Override
    public void run() {
        for (int i = 0; i < NotQuiteModded.particleHandler.activeParticles.size(); i++) {
            NotQuiteModded.particleHandler.activeParticles.get(i).LogicTick();
        }
    }

}
