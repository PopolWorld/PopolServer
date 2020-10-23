package me.nathanfallet.popolserver.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;

public class PopolMenu {

    // Store cached servers
    private static APIServer[] cache;

    // Menu title (constant)
    private static final String title = "Menu";

    // Create menu
    public static void openMenu(final Player player) {
        // Fetch servers
        PopolServer.getInstance().getConnector().getServer(new CompletionHandler<APIServer[]>() {
            @Override
            public void completionHandler(APIServer[] object, APIResponseStatus status) {
                // Check status
                if (status == APIResponseStatus.ok && object != null) {
                    // Cache servers
                    cache = object;

                    // Create menu
                    Inventory menu = Bukkit.createInventory(null, 29, title);

                    // Fill menu with servers
                    for (APIServer server : object) {
                        // If slot is specified
                        if (server.slot != -1) {
                            // Create item
                            ItemStack item = new ItemStack(Material.getMaterial(server.icon));
                            ItemMeta meta = item.getItemMeta();

                            // Set server name
                            meta.setDisplayName(ChatColor.GOLD + server.name);

                            // Create description
                            List<String> lore = new ArrayList<>();
                            lore.add("");
                            lore.add(ChatColor.AQUA + "" + server.players + " joueurs");
                            lore.add("");
                            lore.add(server.status.equals("online") ? (ChatColor.GREEN + "En ligne") : (ChatColor.RED + "Hors ligne"));

                            // Set description
                            meta.setLore(lore);

                            // Set meta back and add to menu
                            item.setItemMeta(meta);
                            menu.setItem(server.slot, item);
                        }
                    }

                    // Open menu
                    player.openInventory(menu);
                }
            }
        });
    }

    // Handle click in menu
    public static void handleClick(InventoryClickEvent event) {
        // Check inventory title and entity
        if (event.getView().getTitle().equals(title) && event.getWhoClicked() instanceof Player && cache != null) {
            // Cancel event
            event.setCancelled(true);

            // Get player
            Player player = (Player) event.getWhoClicked();

            // Check which server is selected (from cache)
            for (APIServer server : cache) {
                // Check slot
                if (event.getSlot() == server.slot) {
                    // Check if server is online
                    if (server.status.equals("online")) {
                        // Send an informative message
                        player.sendMessage(ChatColor.GREEN + "Connexion au serveur " + server.name + "...");

                        // Connect player to this server
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("PopolConnect");
                        out.writeUTF(server.id);
                        player.sendPluginMessage(PopolServer.getInstance(), "BungeeCord", out.toByteArray());
                    } else {
                        // Server is offline
                        player.sendMessage(ChatColor.RED + "Ce serveur est hors ligne !");
                    }

                    // Stop here
                    return;
                }
            }

            // No server found
            player.sendMessage(ChatColor.RED + "Ce serveur n'existe pas !");
        }
    }

}
