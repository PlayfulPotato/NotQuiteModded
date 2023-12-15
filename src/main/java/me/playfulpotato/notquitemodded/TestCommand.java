package me.playfulpotato.notquitemodded;

import me.playfulpotato.notquitemodded.block.TestBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player commandSenderReal = (Player) commandSender;
            NotQuiteModded.testBlock.Place(commandSenderReal.getLocation().toCenterLocation());
            commandSenderReal.getInventory().addItem(NotQuiteModded.testItem.baseItemStack);
        }
        return true;
    }
}
