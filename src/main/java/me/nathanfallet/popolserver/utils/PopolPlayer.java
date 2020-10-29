package me.nathanfallet.popolserver.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIPlayer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;

public class PopolPlayer {

    // Properties
    private UUID uuid;
    private PlayerScoreboard scoreboard;

    // Cached current player
    private APIPlayer cached;

    // Bukkit constructor
    public PopolPlayer(final Player player) {
        // Set properties
        this.uuid = player.getUniqueId();
        this.scoreboard = new PlayerScoreboard("PopolWorld");

        // Update player data
        put(new APIPlayer(uuid.toString(), player.getName(), null, null, null), new CompletionHandler<APIPlayer>() {
            @Override
            public void completionHandler(APIPlayer object, APIResponseStatus status) {
                // Check response
                if (object != null && (status == APIResponseStatus.ok || status == APIResponseStatus.created)) {
                    // Check for first join to welcome player
                    if (status == APIResponseStatus.created) {
                        // Broadcast welcome message
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Bienvenue " + ChatColor.GOLD + player.getName()
                                + ChatColor.YELLOW + " sur " + ChatColor.GOLD + "PopolWorld" + ChatColor.YELLOW + " !");
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
    }

    // Retrieve UUID
    public UUID getUUID() {
        return uuid;
    }

    // Retrieve scoreboard
    public PlayerScoreboard getScoreboard() {
        return scoreboard;
    }

    // Retrieve cache
    public APIPlayer getCached() {
        return cached;
    }

    // Get update from API
    public void get(final CompletionHandler<APIPlayer> completionHandler) {
        // Fetch player data
        PopolServer.getInstance().getConnector().getPlayer(uuid.toString(), new CompletionHandler<APIPlayer>() {
            @Override
            public void completionHandler(APIPlayer object, APIResponseStatus status) {
                // Update cache
                cached = object;

                // Call completion handler
                completionHandler.completionHandler(object, status);
            }
        });
    }

    // Send update to API
    public void put(APIPlayer player, final CompletionHandler<APIPlayer> completionHandler) {
        // Update player data
        PopolServer.getInstance().getConnector().putPlayer(player, new CompletionHandler<APIPlayer>() {
            @Override
            public void completionHandler(APIPlayer object, APIResponseStatus status) {
                // Update cache
                cached = object;

                // Call completion handler
                completionHandler.completionHandler(object, status);
            }
        });
    }

}
