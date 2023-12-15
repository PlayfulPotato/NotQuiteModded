package me.playfulpotato.notquitemodded.particle;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;

public abstract class NQMParticle {

    public TextDisplay particleEntity;
    public Vector velocity;
    public Location particleLocation;
    public int maxTimeLeft;
    public int timeLeft;
    public Component textComponent;
    public Transformation baseTransformation = new Transformation(new Vector3f(0, 0, 0), new AxisAngle4f(1, 0, 0, 0), new Vector3f(1, 1, 1), new AxisAngle4f(1f, 0, 0, 0));
    public final boolean hasTextEntity;
    public boolean OddTick;

    public NQMParticle(int displayLife, boolean hasEntity, Component textComponent) {
        this.maxTimeLeft = displayLife;
        this.timeLeft = displayLife;
        this.hasTextEntity = hasEntity;
        if (textComponent != null) {
            this.textComponent = textComponent;
        }
    }

    public void LogicTick() {
        if (timeLeft == (maxTimeLeft - 1))
            OnParticleCreation();
        timeLeft--;
        particleLocation.add(velocity);

        if (particleEntity != null)
            Objects.requireNonNull(Bukkit.getEntity(particleEntity.getUniqueId())).teleport(particleLocation);

        if (timeLeft <= 0) {
            Destroy();
            return;
        }

        Tick();
    }

    public void Destroy() {
        OnParticleDestroy();
        NotQuiteModded.GetParticleHandler().RemoveParticle(this);
        if (particleEntity != null)
            Objects.requireNonNull(Bukkit.getEntity(particleEntity.getUniqueId())).remove();
    }

    public void Tick() { }
    public void OnParticleCreation() { }
    public void OnParticleDestroy() { }

}
