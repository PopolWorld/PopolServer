package me.nathanfallet.popolserver.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import me.nathanfallet.popolserver.utils.PopolPlayer;

public class PopolPlayerLoadedEvent extends PlayerEvent {

    // Bukkit requirement
    private static final HandlerList handlers = new HandlerList();

    // Properties
    private PopolPlayer pp;

    // Constructor
    public PopolPlayerLoadedEvent(Player player, PopolPlayer pp) {
        // Init event
        super(player);

        // Store data
        this.pp = pp;
    }

    // Bukkit requirement
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    // Get PopolPlayer
    public PopolPlayer getPopolPlayer() {
        return pp;
    }

}
