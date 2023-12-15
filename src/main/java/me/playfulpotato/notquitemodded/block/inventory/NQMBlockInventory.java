package me.playfulpotato.notquitemodded.block.inventory;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NQMBlockInventory implements InventoryHolder {

    public final Plugin plugin;
    public final NQMBlock blockType;
    public final Location inventoryLocation;
    protected Inventory inventory;
    public NQMBlockInventory(@NotNull Plugin plugin, @NotNull NQMBlock blockType, @NotNull Location inventoryLocation) {
        this.plugin = plugin;
        this.blockType = blockType;
        this.inventoryLocation = inventoryLocation.clone().toCenterLocation();
        this.inventoryLocation.setYaw(0);
        this.inventoryLocation.setPitch(0);
        this.inventory = plugin.getServer().createInventory(this, 27);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Syncs the inventory across players who currently have it open.
     */
    public void Sync() {
        if (NotQuiteModded.GetBlockHandler().BlockTypeFromLocation(inventoryLocation) == null)
            return;
        Location location1 = new Location(this.inventoryLocation.getWorld(), this.inventoryLocation.getBlockX(), this.inventoryLocation.getBlockY(), this.inventoryLocation.getBlockZ(), 0, 0);
        for (Player syncCheckPlayer : Bukkit.getOnlinePlayers()) {
            InventoryHolder holder = syncCheckPlayer.getOpenInventory().getTopInventory().getHolder(false);
            if (holder == null)
                continue;
            if (!(holder instanceof NQMBlockInventory))
                continue;
            NQMBlockInventory nqmInventory = (NQMBlockInventory) holder;
            Location location2 = new Location(nqmInventory.inventoryLocation.getWorld(), nqmInventory.inventoryLocation.getBlockX(), nqmInventory.inventoryLocation.getBlockY(), nqmInventory.inventoryLocation.getBlockZ(), 0, 0);
            if (location1.equals(location2)) {
                syncCheckPlayer.openInventory(this.getInventory());
            }
        }
    }
    public void SetCustomKeyValueFromBlock(@NotNull String keyString, @NotNull Object setValue) {
        Chunk getterChunk = inventoryLocation.getChunk();
        PersistentDataContainer chunkStorage = getterChunk.getPersistentDataContainer();

        if (chunkStorage.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + inventoryLocation.getBlockX() + "/" + inventoryLocation.getBlockY() + "/" + inventoryLocation.getBlockZ()), PersistentDataType.INTEGER)) {
            int ID = chunkStorage.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + inventoryLocation.getBlockX() + "/" + inventoryLocation.getBlockY() + "/" + inventoryLocation.getBlockZ()), PersistentDataType.INTEGER);

            Pair<PersistentDataType, Object> dataPair = blockType.uniqueDataPairs.get(keyString);
            chunkStorage.set(new NamespacedKey(this.plugin, keyString + "_" + ID), dataPair.getLeft(), setValue);
        }
    }
    public @Nullable Object GetCustomKeyValueFromBlock(@NotNull String keyString) {
        Chunk getterChunk = inventoryLocation.getChunk();
        PersistentDataContainer chunkStorage = getterChunk.getPersistentDataContainer();

        if (chunkStorage.has(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + inventoryLocation.getBlockX() + "/" + inventoryLocation.getBlockY() + "/" + inventoryLocation.getBlockZ()), PersistentDataType.INTEGER)) {
            int ID = chunkStorage.get(new NamespacedKey(NotQuiteModded.GetPlugin(), "Block_" + inventoryLocation.getBlockX() + "/" + inventoryLocation.getBlockY() + "/" + inventoryLocation.getBlockZ()), PersistentDataType.INTEGER);

            Pair<PersistentDataType, Object> dataPair = blockType.uniqueDataPairs.get(keyString);
            return chunkStorage.get(new NamespacedKey(this.plugin, keyString + "_" + ID), dataPair.getLeft());
        }
        return null;
    }
    public abstract void InventoryClicked(InventoryClickEvent event);

}
