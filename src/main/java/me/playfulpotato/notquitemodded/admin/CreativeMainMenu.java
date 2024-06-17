package me.playfulpotato.notquitemodded.admin;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.block.NQMBlockFactory;
import me.playfulpotato.notquitemodded.inventory.NQMInventory;
import me.playfulpotato.notquitemodded.item.NQMItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CreativeMainMenu extends NQMInventory {

    public CreativeMainMenu() {
        super(NotQuiteModded.GetPlugin(), 27, Component.text("Creative Menu"));
        ItemStack panelGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = panelGlass.getItemMeta();
        meta.setHideTooltip(true);
        panelGlass.setItemMeta(meta);

        ItemStack darkPanelGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta = darkPanelGlass.getItemMeta();
        meta.setHideTooltip(true);
        darkPanelGlass.setItemMeta(meta);

        for (int i = 0; i < 10; i++) {
            inventory.setItem(i, darkPanelGlass);
        }
        for (int i = 10; i < 17; i++) {
            inventory.setItem(i, panelGlass);
        }
        for (int i = 17; i < inventory.getSize(); i++) {
            inventory.setItem(i, darkPanelGlass);
        }

        ItemStack itemMenuClickable = new ItemStack(Material.EMERALD);
        meta = itemMenuClickable.getItemMeta();
        meta.displayName(Component.text("Items").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        itemMenuClickable.setItemMeta(meta);
        List<Component> itemLore = new ArrayList<>();
        itemLore.add(Component.text("Opens the item menu.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        itemLore.add(Component.text("Contains all registered items.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        itemMenuClickable.lore(itemLore);

        ItemStack blockMenuClickable = new ItemStack(Material.EMERALD_BLOCK);
        meta = blockMenuClickable.getItemMeta();
        meta.displayName(Component.text("Blocks").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        blockMenuClickable.setItemMeta(meta);
        itemLore = new ArrayList<>();
        itemLore.add(Component.text("Opens the block menu.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        itemLore.add(Component.text("Contains all factories that make blocks.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        blockMenuClickable.lore(itemLore);

        inventory.setItem(12, itemMenuClickable);
        inventory.setItem(14, blockMenuClickable);
    }

    @Override
    public void InventoryClicked(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            final int slot = event.getSlot();
            Bukkit.getScheduler().runTask(NotQuiteModded.GetPlugin(), () -> {
                if (slot == 12) {
                    List<NQMItem> itemList = NotQuiteModded.itemHandler.NQMItemRegistry.values().stream().sorted(Comparator.comparing(item -> item.fullStorageKey)).toList();
                    player.openInventory(new CreativeItemMenu(1, itemList).getInventory());
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
                } else if (slot == 14) {
                    List<NQMBlockFactory> factoryList = NotQuiteModded.blockHandler.factoryMap.values().stream().sorted(Comparator.comparing(factory -> factory.storageKey)).toList();
                    player.openInventory(new CreativeBlockMenu(1, factoryList).getInventory());
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
                }
            });
        }
    }
}
