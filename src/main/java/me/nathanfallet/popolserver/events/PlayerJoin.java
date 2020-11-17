package me.nathanfallet.popolserver.events;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;

import me.nathanfallet.popolserver.BungeePopolServer;
import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;

public class PlayerJoin implements org.bukkit.event.Listener, net.md_5.bungee.api.plugin.Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update player data
        PopolServer.getInstance().getPlayers().add(new PopolPlayer(event.getPlayer()));

        // Set join message
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.GOLD
                + event.getPlayer().getName());

        // Send welcome message
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Bienvenue sur PopolWorld ! Faites " + ChatColor.GOLD
                + "/menu " + ChatColor.YELLOW + "pour changer de serveur.");

        // Teleport to spawn if first join
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().teleport(PopolServer.getInstance().getSpawn());
        }

        // Update status
        PopolServer.getInstance().sendStatus();
    }

    @net.md_5.bungee.event.EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        // Update status
        BungeePopolServer.getInstance().sendStatus();
    }

}
