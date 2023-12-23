package me.playfulpotato.notquitemodded.particle;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticleHandler {
    public List<NQMParticle> activeParticles = new ArrayList<>();
    public List<NQMParticle> EvenTick = new ArrayList<>();
    public List<NQMParticle> OddTick = new ArrayList<>();

    public void CreateParticle(@NotNull NQMParticle creationParticle, @NotNull Location spawnLocation, @NotNull Vector velocity, boolean ignoreLightLevels, boolean force) {

        if (!force) {
            if (SpawnConditionsNotMet(spawnLocation))
                return;
        } else {
            if (!spawnLocation.getChunk().isLoaded())
                return;
        }

        activeParticles.add(creationParticle);
        creationParticle.particleLocation = spawnLocation.clone();
        creationParticle.velocity = velocity;
        if (creationParticle.hasTextEntity) {
            creationParticle.particleEntity = CreateParticleEntity(ignoreLightLevels, spawnLocation, creationParticle);
        }
        KickstartTicks();
        creationParticle.OddTick = !ParticleTicker.OddTick;
        if (creationParticle.OddTick) {
            OddTick.add(creationParticle);
        } else {
            EvenTick.add(creationParticle);
        }
    }
    public void CreateParticle(@NotNull NQMParticle creationParticle, @NotNull Location spawnLocation, @NotNull Vector velocity, boolean ignoreLightLevels) {

        if (SpawnConditionsNotMet(spawnLocation))
            return;

        activeParticles.add(creationParticle);
        creationParticle.particleLocation = spawnLocation.clone();
        creationParticle.velocity = velocity;
        if (creationParticle.hasTextEntity) {
            creationParticle.particleEntity = CreateParticleEntity(ignoreLightLevels, spawnLocation, creationParticle);
        }
        KickstartTicks();
        creationParticle.OddTick = !ParticleTicker.OddTick;
        if (creationParticle.OddTick) {
            OddTick.add(creationParticle);
        } else {
            EvenTick.add(creationParticle);
        }
    }
    public void CreateParticle(@NotNull NQMParticle creationParticle, @NotNull Location spawnLocation, @NotNull Vector velocity) {

        if (SpawnConditionsNotMet(spawnLocation))
            return;

        activeParticles.add(creationParticle);
        creationParticle.particleLocation = spawnLocation.clone();
        creationParticle.velocity = velocity;
        if (creationParticle.hasTextEntity) {
            creationParticle.particleEntity = CreateParticleEntity(false, spawnLocation, creationParticle);
        }
        KickstartTicks();
        creationParticle.OddTick = !ParticleTicker.OddTick;
        if (creationParticle.OddTick) {
            OddTick.add(creationParticle);
        } else {
            EvenTick.add(creationParticle);
        }
    }

    public void CreateParticle(@NotNull NQMParticle creationParticle, @NotNull Location spawnLocation) {

        if (SpawnConditionsNotMet(spawnLocation))
            return;

        activeParticles.add(creationParticle);
        creationParticle.particleLocation = spawnLocation.clone();
        creationParticle.velocity = new Vector(0, 0, 0);
        if (creationParticle.hasTextEntity) {
            creationParticle.particleEntity = CreateParticleEntity(false, spawnLocation, creationParticle);
        }
        KickstartTicks();
        creationParticle.OddTick = !ParticleTicker.OddTick;
        if (creationParticle.OddTick) {
            OddTick.add(creationParticle);
        } else {
            EvenTick.add(creationParticle);
        }
    }

    public void DestroyAllParticles() {
        EvenTick.clear();
        OddTick.clear();
        for (int i = 0; i < activeParticles.size(); i++) {
            activeParticles.get(i).Destroy();
        }
    }

    private boolean SpawnConditionsNotMet(@NotNull Location locationCheck) {
        boolean chunkLoaded = locationCheck.getChunk().isLoaded();
        Collection<Player> nearbyPlayers = locationCheck.getNearbyPlayers(20);
        return !chunkLoaded || nearbyPlayers.size() == 0;
    }
    private @NotNull TextDisplay CreateParticleEntity(boolean ignoreLightLevels, @NotNull Location creationLocation, @NotNull NQMParticle particleType) {
        Location spawnLocation = creationLocation.clone();
        spawnLocation.setPitch(0);
        spawnLocation.setYaw(0);

        TextDisplay particleEntity = (TextDisplay) creationLocation.getWorld().spawnEntity(spawnLocation, EntityType.TEXT_DISPLAY);
        particleEntity.setBillboard(Display.Billboard.CENTER);
        particleEntity.setTeleportDuration(10);
        particleEntity.setShadowRadius(0);
        if (ignoreLightLevels)
            particleEntity.setBrightness(new Display.Brightness(15, 15));
        particleEntity.setSeeThrough(false);
        particleEntity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        particleEntity.setInterpolationDelay(0);
        particleEntity.setTransformation(particleType.baseTransformation);
        particleEntity.getPersistentDataContainer().set(new NamespacedKey(NotQuiteModded.GetPlugin(), "DestroyOnChunkLoad"), PersistentDataType.BOOLEAN, true);
        if (particleType.textComponent != null)
            particleEntity.text(particleType.textComponent);
        return particleEntity;
    }

    private void KickstartTicks() {
        if (ParticleTicker.particleTicker == null) {
            new ParticleTicker();
        }
    }

    public List<NQMParticle> GetActiveParticles() {
        return activeParticles;
    }
    public void RemoveParticle(@NotNull NQMParticle removalParticle) {
        activeParticles.remove(removalParticle);
        if (removalParticle.OddTick) {
            OddTick.remove(removalParticle);
        } else {
            EvenTick.remove(removalParticle);
        }
        if (activeParticles.size() == 0 && ParticleTicker.particleTicker != null) {
            ParticleTicker.OddTick = false;
            ParticleTicker.particleTicker.cancel();
            ParticleTicker.particleTicker = null;
        }
    }
}
