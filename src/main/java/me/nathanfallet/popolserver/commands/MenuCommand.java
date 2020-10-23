package me.nathanfallet.popolserver.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.utils.PopolMenu;

public class MenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check is sender is a player
        if (sender instanceof Player) {
            // Open menu
            PopolMenu.openMenu((Player) sender);
        }
        return true;
    }

}
