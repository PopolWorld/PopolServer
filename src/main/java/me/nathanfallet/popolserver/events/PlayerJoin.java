package me.nathanfallet.popolserver.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import me.nathanfallet.popolserver.BungeePopolServer;
import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIPlayer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import net.md_5.bungee.api.event.PostLoginEvent;

public class PlayerJoin implements org.bukkit.event.Listener, net.md_5.bungee.api.plugin.Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update status
        PopolServer.getInstance().sendStatus();

        // Update player data
        final Player player = event.getPlayer();
        PopolServer.getInstance().getConnector().putPlayer(
                new APIPlayer(player.getUniqueId().toString(), player.getName(), null, null, null),
                new CompletionHandler<APIPlayer>() {
                    @Override
                    public void completionHandler(APIPlayer object, APIResponseStatus status) {
                        // Check response
                        if (object != null && (status == APIResponseStatus.ok || status == APIResponseStatus.created)) {
                            // Check for first join to welcome player
                            if (status == APIResponseStatus.created) {
                                // Broadcast welcome message
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "Bienvenue " + ChatColor.GOLD
                                        + player.getName() + ChatColor.YELLOW + " sur " + ChatColor.GOLD + "PopolWorld"
                                        + ChatColor.YELLOW + " !");
                            }

                            // Check for staff level
                            // TODO

                            // Set operator
                            player.setOp(object.administrator != null && object.administrator.booleanValue());
                        } else {
                            // Error, disconnect player
                            player.kickPlayer("Erreur lors de la vérification de votre identité dans la base de données !");
                        }
                    }
                });

        // Send welcome message
        player.sendMessage(ChatColor.YELLOW + "Bienvenue sur PopolWorld ! Faites " + ChatColor.GOLD + "/menu "
                + ChatColor.YELLOW + "pour changer de serveur.");
    }

    @net.md_5.bungee.event.EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        // Update status
        BungeePopolServer.getInstance().sendStatus();
    }

}
