package me.playfulpotato.notquitemodded.particle;

import java.util.ArrayList;
import java.util.List;

public class ParticleHandler {
    public List<NQMParticle> activeParticles = new ArrayList<>();

    public ParticleHandler() {
        new ParticleTicker();
    }

    public void DestroyAllParticles() {
        for (int i = 0; i < activeParticles.size(); i++) {
            activeParticles.get(i).Destroy();
        }
    }
}
