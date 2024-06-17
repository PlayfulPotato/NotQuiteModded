package me.playfulpotato.notquitemodded.item.listeners;

import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import me.playfulpotato.notquitemodded.NotQuiteModded;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.NQMItem;
import org.apache.logging.log4j.core.net.Priority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class PlayerStopUsingItemCheck implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerStopUsingItem(PlayerStopUsingItemEvent event) {

        if (event.getItem().getItemMeta().getPersistentDataContainer().has(ItemHandler.itemTypeKey))
            return;

        String itemStorageKey = event.getItem().getItemMeta().getPersistentDataContainer().get(ItemHandler.itemTypeKey, PersistentDataType.STRING);
        if (itemStorageKey == null)
            return;

        NQMItem itemType = NotQuiteModded.GetItemHandler().ItemTypeFromStorageKey(itemStorageKey);
        itemType.StopUsing(event.getPlayer(), event);

    }

}
