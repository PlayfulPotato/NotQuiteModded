package me.playfulpotato.notquitemodded.player.saves;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class PlayerSaveHelper {
    /**
     * Creates a player save that can be loaded later. Saves exp and items. Nothing else is saved, if you wish to save more you would need to make your own system.
     * @param plugin The plugin that is creating the save data.
     * @param saveKey The key to save the save under.
     * @param player The player to create the save for.
     */
    public static void CreatePlayerSave(@NotNull Plugin plugin, @NotNull String saveKey, @NotNull Player player) {
        try {
            PlayerInventory playerInventory = player.getInventory();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(playerInventory.getSize());

            for (int i = 0; i < playerInventory.getSize(); i++) {
                dataOutput.writeObject(playerInventory.getItem(i));
            }

            dataOutput.writeFloat(player.getExp());

            dataOutput.writeInt(player.getLevel());

            dataOutput.flush();

            byte[] rawSaveData = outputStream.toByteArray();
            String saveString = Base64.getEncoder().encodeToString(rawSaveData);

            player.getPersistentDataContainer().set(new NamespacedKey(plugin, saveKey), PersistentDataType.STRING, saveString);

            dataOutput.close();

        } catch (Exception e) {
            NotQuiteModded.GetPlugin().getLogger().info("Unable to save data for " + player.getName() + " under " + saveKey + "!");
        }
    }

    /**
     * Loads a players save data from a previous save.
     * @param plugin The plugin that made the save.
     * @param saveKey The key the save is stored under.
     * @param player The player that has the save.
     */
    public static void LoadPlayerSave(@NotNull Plugin plugin, @NotNull String saveKey, @NotNull Player player) {
        try {
            if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, saveKey)))
                return;

            byte[] rawLoadData = Base64.getDecoder().decode(player.getPersistentDataContainer().get(new NamespacedKey(plugin, saveKey), PersistentDataType.STRING));

            ByteArrayInputStream inputStream = new ByteArrayInputStream(rawLoadData);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            player.getInventory().clear();

            int InventorySize = dataInput.readInt();

            for (int i = 0; i < InventorySize; i++) {
                player.getInventory().setItem(i, (ItemStack) dataInput.readObject());
            }

            player.setExp(dataInput.readFloat());
            player.setLevel(dataInput.readInt());

            dataInput.close();

        } catch (Exception e) {
            NotQuiteModded.GetPlugin().getLogger().info("Unable to load data for " + player.getName() + " under " + saveKey + "!");
        }
    }

}
