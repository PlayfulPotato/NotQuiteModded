package me.playfulpotato.notquitemodded.item;

import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import me.playfulpotato.notquitemodded.NotQuiteModded;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class NQMItem {

    public boolean DisableMostCraftingUse = true;
    public final Plugin plugin;
    public final String storageKey;
    public final String fullStorageKey;
    public final ItemGeneralization itemType;
    public ItemStack baseItemStack;

    public NQMItem(@NotNull Plugin plugin, @NotNull String storageKey, @NotNull Material baseItemMaterial, String baseItemName, @NotNull ItemGeneralization itemType) {
        this.plugin = plugin;
        this.storageKey = storageKey;
        this.fullStorageKey = plugin.getName() + ":" + storageKey;
        this.itemType = itemType;
        this.baseItemStack = new ItemStack(baseItemMaterial);
        ItemMeta itemStackMeta = this.baseItemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemStackMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(NotQuiteModded.GetPlugin(), "itemType"), PersistentDataType.STRING, this.fullStorageKey);
        if (baseItemName != null) {
            itemStackMeta.itemName(Component.text(baseItemName).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        }
        this.baseItemStack.setItemMeta(itemStackMeta);
    }
    public void RightClick(@NotNull Player player, @NotNull PlayerInteractEvent event) { }
    public void LeftClick(@NotNull Player player, @NotNull PlayerInteractEvent event) { }
    public void Swap(@NotNull Player player, @NotNull PlayerSwapHandItemsEvent event) { }
    public void Consume(@NotNull Player player, @NotNull PlayerItemConsumeEvent event) { }
    public void Place(@NotNull Player player, @NotNull BlockPlaceEvent event) { }
    public void StopUsing(@NotNull Player player, @NotNull PlayerStopUsingItemEvent event) { }

    public ItemStack getItemForCreative() {return baseItemStack;}

}
