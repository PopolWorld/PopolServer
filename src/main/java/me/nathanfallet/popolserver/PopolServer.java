package me.nathanfallet.popolserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.PopolConnector;
import me.nathanfallet.popolserver.commands.MenuCommand;
import me.nathanfallet.popolserver.events.InventoryClick;
import me.nathanfallet.popolserver.events.PlayerJoin;
import me.nathanfallet.popolserver.events.PlayerQuit;
import me.nathanfallet.popolserver.utils.PopolPlayer;

public class PopolServer extends JavaPlugin {

    // Static instance
    private static PopolServer instance;

    // Retrieve instance
    public static PopolServer getInstance() {
        return instance;
    }

    // Properties
    private PopolConnector connector;
    private List<PopolPlayer> players;

    // Enable plugin
    @Override
    public void onEnable() {
        // Store instance
        instance = this;

        // Init players
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayers().add(new PopolPlayer(player));
        }

        // Register events
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);

        // Register commands
        getCommand("menu").setExecutor(new MenuCommand());

        // Send status
        sendStatus();
    }

    // Disable plugin
    @Override
    public void onDisable() {
        // Remove players
        getPlayers().clear();

        // Send status
        sendStatus();
    }

    // Retrieve connector
    public PopolConnector getConnector() {
        // Load connector if needed
        if (connector == null) {
            try {
                // Create connector with config
                connector = new PopolConnector(getConfig().getString("id"), getConfig().getString("token"));
            } catch (Exception e) {
                // Error
                e.printStackTrace();

                // Stop server
                Bukkit.shutdown();
            }
        }

        // Return connector
        return connector;
    }

    // Retrieve players
    public List<PopolPlayer> getPlayers() {
        // Init players if needed
        if (players == null) {
            players = new ArrayList<>();
        }

        // Return players
        return players;
    }

    // Retrieve a player from its UUID
    public PopolPlayer getPlayer(UUID uuid) {
        // Iterate players
        for (PopolPlayer player : getPlayers()) {
            if (player.getUUID().equals(uuid)) {
                return player;
            }
        }

        // No player found
        return null;
    }

    // Send current server status
    public void sendStatus() {
        getConnector().putServer(new APIServer(null, null, null, null, null, null, "online", getPlayers().size()));
    }

}
