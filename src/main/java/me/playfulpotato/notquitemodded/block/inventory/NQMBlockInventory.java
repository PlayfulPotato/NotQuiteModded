package me.playfulpotato.notquitemodded.block.inventory;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlock;
import me.playfulpotato.notquitemodded.inventory.NQMInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class NQMBlockInventory extends NQMInventory {

    public final NQMBlock block;
    public NQMBlockInventory(@NotNull Plugin plugin, int size, @NotNull Component name, @NotNull NQMBlock block) {
        super(plugin, size, name);
        this.block = block;
    }

    /**
     * Syncs the inventory across players who currently have it open.
     */
    public void Sync() {
        if (NotQuiteModded.GetBlockHandler().getNQMBlock(block.blockLocation) == null)
            return;
        for (Player syncCheckPlayer : Bukkit.getOnlinePlayers()) {
            InventoryHolder holder = syncCheckPlayer.getOpenInventory().getTopInventory().getHolder(false);
            if (holder == null)
                continue;
            if (!(holder instanceof NQMBlockInventory nqmBlockInventory))
                continue;
            if (block.equals(nqmBlockInventory.block)) {
                if (holder.getInventory().getSize() != this.getInventory().getSize()) {
                    syncCheckPlayer.openInventory(this.getInventory());
                } else {
                    Inventory inventorySync = this.getInventory();
                    for (int index = 0; index < inventorySync.getSize(); index++) {
                        ItemStack syncItem = inventorySync.getItem(index);
                        if (syncItem == null)
                            continue;
                        holder.getInventory().setItem(index, syncItem);
                    }
                }
            }
        }
    }

}
