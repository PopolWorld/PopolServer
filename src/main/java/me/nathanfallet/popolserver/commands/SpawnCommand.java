package me.nathanfallet.popolserver.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.PopolServer;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Check args
		if (args.length == 1 && sender.hasPermission("popolserver.spawn.others")) {
			// Try to teleport someone else
			Player p = Bukkit.getPlayer(args[0]);

			// Check player
			if (p != null && p.isOnline()) {
				p.sendMessage(ChatColor.GOLD + "Téléportation au spawn...");
				p.teleport(PopolServer.getInstance().getSpawn());
			}
		} else if (sender instanceof Player) {
			// Teleport sender
			Player p = (Player) sender;
			p.sendMessage(ChatColor.GOLD + "Téléportation au spawn...");
			p.teleport(PopolServer.getInstance().getSpawn());
		}
		return true;
	}

}
