package me.playfulpotato.notquitemodded.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreativeMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 0.8f, 1.3f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.2f, 1.7f);
            player.openInventory(new CreativeMainMenu().getInventory());
            return true;
        }
        commandSender.sendMessage(Component.text("This command needs to be run as a player!").color(NamedTextColor.RED));
        return true;
    }
}
