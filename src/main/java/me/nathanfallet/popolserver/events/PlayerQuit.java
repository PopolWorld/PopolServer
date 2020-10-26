package me.nathanfallet.popolserver.events;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerQuitEvent;

import me.nathanfallet.popolserver.BungeePopolServer;
import me.nathanfallet.popolserver.PopolServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;

public class PlayerQuit implements org.bukkit.event.Listener, net.md_5.bungee.api.plugin.Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player data
        PopolServer.getInstance().getPlayers()
                .remove(PopolServer.getInstance().getPlayer(event.getPlayer().getUniqueId()));

        // Set quit message
        event.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.GOLD
                + event.getPlayer().getName());

        // Update status
        PopolServer.getInstance().sendStatus();
    }

    @net.md_5.bungee.event.EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        // Update status
        BungeePopolServer.getInstance().sendStatus();
    }

}
