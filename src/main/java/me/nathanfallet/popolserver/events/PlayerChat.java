package me.nathanfallet.popolserver.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Get player
        Player player = event.getPlayer();

        // Set custom chat format
        event.setFormat("%s" + ChatColor.RESET + ": %s");

        // If player is operator enable colors
        if (player.isOp()) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }

        // Manage mentions
        event.setMessage(" " + event.getMessage() + " ");
        for (Player current : Bukkit.getOnlinePlayers()) {
            if (event.getMessage().contains(" " + current.getName() + " ")) {
                // Play a sound and color the name
                current.playSound(current.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
                event.setMessage(event.getMessage().replaceAll(" " + current.getName() + " ",
                        " " + ChatColor.AQUA + "@" + current.getName() + ChatColor.RESET + " "));
            }
        }

        // Update message
        event.setMessage(event.getMessage().trim());
    }

}
