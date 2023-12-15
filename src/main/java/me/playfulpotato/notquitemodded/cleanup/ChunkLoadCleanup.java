package me.playfulpotato.notquitemodded.cleanup;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ChunkLoadCleanup {

    private static final NamespacedKey deleteKey = new NamespacedKey(NotQuiteModded.GetPlugin(), "DestroyOnChunkLoad");

    public static void CleanupChunk(@NotNull Chunk cleanupChunk) {
        Entity[] chunkEntities = cleanupChunk.getEntities();
        for (int i = 0; i < chunkEntities.length; i++) {
            Entity chunkEntity = chunkEntities[i];
            if (chunkEntity.getPersistentDataContainer().has(deleteKey))
                chunkEntity.remove();
        }
    }

}
