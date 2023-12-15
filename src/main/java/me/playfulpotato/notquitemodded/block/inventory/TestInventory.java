package me.playfulpotato.notquitemodded.block.inventory;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestInventory extends NQMBlockInventory {
    public TestInventory(@NotNull Location blockLocation) {
        super(NotQuiteModded.GetPlugin(), NotQuiteModded.GetTestBlock(), blockLocation);
        if (GetCustomKeyValueFromBlock("testKey") != null)
            inventory.setItem(0, new ItemStack(Material.TORCHFLOWER, (int)GetCustomKeyValueFromBlock("testKey")));
    }
    @Override
    public void InventoryClicked(InventoryClickEvent event) {
        event.setCancelled(true);
        if (GetCustomKeyValueFromBlock("testKey") == null)
            return;

        int currentTestKeyValue = (int) GetCustomKeyValueFromBlock("testKey");
        currentTestKeyValue++;
        SetCustomKeyValueFromBlock("testKey", currentTestKeyValue);
        inventory.setItem(0, new ItemStack(Material.TORCHFLOWER, currentTestKeyValue));
        Sync();
    }
}
