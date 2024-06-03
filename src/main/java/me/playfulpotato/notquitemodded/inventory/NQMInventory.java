package me.playfulpotato.notquitemodded.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class NQMInventory implements InventoryHolder {

    protected Inventory inventory;
    public final Plugin plugin;

    public NQMInventory(@NotNull Plugin plugin, int size, @NotNull Component name) {
        this.plugin = plugin;
        this.inventory = plugin.getServer().createInventory(this, size, name);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public abstract void InventoryClicked(InventoryClickEvent event);
    public void InventoryDragged(InventoryDragEvent event) { }

}
