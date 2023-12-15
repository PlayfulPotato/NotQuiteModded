package me.playfulpotato.notquitemodded.item.listeners;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.NQMItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerInteractItemCheck implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void OnPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction().equals(Action.PHYSICAL))
            return;

        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND))
            return;

        if (!event.hasItem())
            return;

        if (!Objects.requireNonNull(event.getItem()).getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey))
            return;

        // This one singular line is a nightmare and is absolutely dog shit.
        NQMItem itemType = NotQuiteModded.GetItemHandler().ItemTypeFromStorageKey(Objects.requireNonNull(Objects.requireNonNull(event.getItem()).getItemMeta().getPersistentDataContainer().get(ItemHandler.itemTypeKey, PersistentDataType.STRING)));

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            itemType.RightClick(event.getPlayer(), event);
        } else {
            itemType.LeftClick(event.getPlayer(), event);
        }
    }
}