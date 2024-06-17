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
import java.util.List;

public class CreativeBlockMenu extends NQMInventory {

    List<NQMBlockFactory> blockFactoryList;
    final int page;
    public CreativeBlockMenu(int page, List<NQMBlockFactory> blockFactoryList) {
        super(NotQuiteModded.GetPlugin(), 36, Component.text("Block Menu"));
        this.page = page;
        this.blockFactoryList = blockFactoryList;

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

        ItemStack itemIcon = new ItemStack(Material.EMERALD_BLOCK);
        meta = itemIcon.getItemMeta();
        meta.displayName(Component.text("Blocks").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        meta.setEnchantmentGlintOverride(true);
        itemIcon.setItemMeta(meta);

        for (int i = 27; i < inventory.getSize(); i++) {
            inventory.setItem(i, darkPanelGlass);
        }
        if (blockFactoryList.size() > (27 * page)) {
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
        if (blockFactoryList.size() <= (27 * page)) {
            limit = blockFactoryList.size();
        }
        int counter = 0;
        for (int i = 27 * (page - 1); i < limit; i++) {
            NQMBlockFactory currentFactory = blockFactoryList.get(i);
            ItemStack putItem = new ItemStack(currentFactory.blockBaseMaterial);
            meta = putItem.getItemMeta();
            meta.displayName(Component.text(currentFactory.storageName).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            putItem.setItemMeta(meta);
            itemLore = putItem.lore();
            if (itemLore == null) {
                itemLore = new ArrayList<>();
            }
            itemLore.add(Component.text("Added by: ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Component.text(currentFactory.plugin.getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)));
            itemLore.add(Component.text("Left Click to place the associated block.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
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
                    player.openInventory(new CreativeBlockMenu(page - 1, blockFactoryList).getInventory());
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.35f, 1.0f);
                    return;
                }
                if (slot == 34 && blockFactoryList.size() > (27 * page)) {
                    player.openInventory(new CreativeBlockMenu(page + 1, blockFactoryList).getInventory());
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.35f, 1.0f);
                    return;
                }
                if (slot < 27 && isLeftClick) {
                    if (blockFactoryList.size() > ((27 * (page-1)) + slot)) {
                        NQMBlockFactory clickedBlockFactory = blockFactoryList.get(slot + (27 * (page - 1)));
                        NotQuiteModded.blockHandler.placeBlock(player.getLocation(), clickedBlockFactory);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                }
            });
        }
    }
}
