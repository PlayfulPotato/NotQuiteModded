package me.playfulpotato.notquitemodded.particle;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class NQMParticle {

    public Vector velocity;
    public Location location;
    public int timeLeft;
    public final int maxTimeLeft;

    public NQMParticle(int timeLeft, @NotNull Vector velocity, @NotNull Location location) {
        this.timeLeft = timeLeft;
        this.maxTimeLeft = timeLeft;
        this.velocity = velocity;
        this.location = location;
        NotQuiteModded.particleHandler.activeParticles.add(this);
    }

    public void LogicTick() {
        if ((maxTimeLeft - 2) == timeLeft)
            CreationEffects();
        location.add(velocity);

        timeLeft--;
        if (timeLeft <= 0) {
            Destroy();
            return;
        }

        ExtraLogic();
        Tick();
    }

    public void Destroy() {
        NotQuiteModded.particleHandler.activeParticles.remove(this);

        ExtraDestroyLogic();
        DestroyOverrideable();
    }
    protected void ExtraDestroyLogic() { }
    public void DestroyOverrideable() { }

    protected void ExtraLogic() { }
    public void Tick() { }

    public void CreationEffects() { }
}
