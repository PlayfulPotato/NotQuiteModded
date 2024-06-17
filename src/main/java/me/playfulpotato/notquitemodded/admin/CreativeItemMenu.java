package me.playfulpotato.notquitemodded.admin;

import me.playfulpotato.notquitemodded.NotQuiteModded;
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
import java.util.List;

public class CreativeItemMenu extends NQMInventory {

    List<NQMItem> itemList;
    final int page;
    public CreativeItemMenu(int page, List<NQMItem> itemList) {
        super(NotQuiteModded.GetPlugin(), 36, Component.text("Item Menu"));
        this.page = page;
        this.itemList = itemList;

        ItemStack darkPanelGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = darkPanelGlass.getItemMeta();
        meta.setHideTooltip(true);
        darkPanelGlass.setItemMeta(meta);

        ItemStack leaveMenuItem = new ItemStack(Material.BARRIER);
        meta = leaveMenuItem.getItemMeta();
        meta.displayName(Component.text("Return").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        leaveMenuItem.setItemMeta(meta);
        List<Component> itemLore = new ArrayList<>();
        itemLore.add(Component.text("Returns to the main creative menu.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        leaveMenuItem.lore(itemLore);

        ItemStack itemIcon = new ItemStack(Material.EMERALD);
        meta = itemIcon.getItemMeta();
        meta.displayName(Component.text("Items").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        meta.setEnchantmentGlintOverride(true);
        itemIcon.setItemMeta(meta);

        for (int i = 27; i < inventory.getSize(); i++) {
            inventory.setItem(i, darkPanelGlass);
        }
        if (itemList.size() > (27 * page)) {
            ItemStack forwardArrow = new ItemStack(Material.ARROW);
            meta = forwardArrow.getItemMeta();
            meta.displayName(Component.text("Next").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            forwardArrow.setItemMeta(meta);
            itemLore = new ArrayList<>();
            itemLore.add(Component.text("Go to the next page.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            forwardArrow.lore(itemLore);

            inventory.setItem(34, forwardArrow);
        }

        if (page > 1) {
            ItemStack backArrow = new ItemStack(Material.ARROW);
            meta = backArrow.getItemMeta();
            meta.displayName(Component.text("Back").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            backArrow.setItemMeta(meta);
            itemLore = new ArrayList<>();
            itemLore.add(Component.text("Go back a page.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            backArrow.lore(itemLore);

            inventory.setItem(28, backArrow);
        }
        inventory.setItem(27, leaveMenuItem);
        inventory.setItem(31, itemIcon);

        int limit = 27 * page;
        if (itemList.size() <= (27 * page)) {
            limit = itemList.size();
        }
        int counter = 0;
        for (int i = 27 * (page - 1); i < limit; i++) {
            ItemStack putItem = itemList.get(i).baseItemStack.clone();
            itemLore = putItem.lore();
            if (itemLore == null) {
                itemLore = new ArrayList<>();
            }
            itemLore.add(Component.text("Added by: ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Component.text(itemList.get(i).plugin.getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)));
            itemLore.add(Component.text("Left Click to get the item.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            putItem.lore(itemLore);
            inventory.setItem(counter, putItem);
            counter++;
        }

    }

    @Override
    public void InventoryClicked(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            final int slot = event.getSlot();
            final boolean isLeftClick = event.isLeftClick();
            Bukkit.getScheduler().runTask(NotQuiteModded.GetPlugin(), () -> {

                if (slot == 27) {
                    player.openInventory(new CreativeMainMenu().getInventory());
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1.0f, 1.0f);
                    return;
                }
                if (slot == 28 && page > 1) {
                    player.openInventory(new CreativeItemMenu(page - 1, itemList).getInventory());
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.35f, 1.0f);
                    return;
                }
                if (slot == 34 && itemList.size() > (27 * page)) {
                    player.openInventory(new CreativeItemMenu(page + 1, itemList).getInventory());
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.35f, 1.0f);
                    return;
                }
                if (slot < 27 && isLeftClick) {
                    if (itemList.size() > ((27 * (page-1)) + slot)) {
                        NQMItem clickedItemType = itemList.get(slot + (27 * (page - 1)));
                        player.getInventory().addItem(clickedItemType.getItemForCreative());
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                }
            });
        }
    }
}
