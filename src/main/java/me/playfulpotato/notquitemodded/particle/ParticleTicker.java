package me.playfulpotato.notquitemodded.particle;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ParticleTicker extends BukkitRunnable {

    public static ParticleTicker particleTicker = null;
    public static boolean OddTick = false;

    public ParticleTicker() {
        if (particleTicker != null)
            return;

        OddTick = false;
        runTaskTimer(NotQuiteModded.GetPlugin(), 1, 1);
        particleTicker = this;
    }

    @Override
    public void run() {
        if (OddTick) {
            List<NQMParticle> oddTickingParticles = NotQuiteModded.GetParticleHandler().OddTick;
            for (int i = 0; i < oddTickingParticles.size(); i++) {
                oddTickingParticles.get(i).LogicTick();
            }
        } else {
            List<NQMParticle> evenTickingParticles = NotQuiteModded.GetParticleHandler().EvenTick;
            for (int i = 0; i < evenTickingParticles.size(); i++) {
                evenTickingParticles.get(i).LogicTick();
            }
        }
        OddTick = !OddTick;
    }

}
