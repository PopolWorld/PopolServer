package me.nathanfallet.popolserver.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.nathanfallet.popolserver.utils.PopolMenu;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check for menu
        PopolMenu.handleClick(event);
    }
    
}
