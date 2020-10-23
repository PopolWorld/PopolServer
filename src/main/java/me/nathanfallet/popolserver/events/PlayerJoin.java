package me.nathanfallet.popolserver.events;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.nathanfallet.popolserver.PopolServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update status
        PopolServer.getInstance().sendStatus();

        // Update player data
        // TODO

        // Send welcome message
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Bienvenue sur PopolWorld ! Faites " + ChatColor.GOLD
                + "/menu " + ChatColor.YELLOW + "pour changer de serveur.");
    }

}
