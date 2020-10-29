package me.nathanfallet.popolserver.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.nathanfallet.popolserver.PopolServer;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Check is spawn location is from bed
        if (!event.isBedSpawn()) {
            // If not, set spawn
            event.setRespawnLocation(PopolServer.getInstance().getSpawn());
        }
    }

}
