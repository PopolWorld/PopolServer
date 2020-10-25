package me.nathanfallet.popolserver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.PopolConnector;
import me.nathanfallet.popolserver.commands.MenuCommand;
import me.nathanfallet.popolserver.events.InventoryClick;
import me.nathanfallet.popolserver.events.PlayerJoin;

public class PopolServer extends JavaPlugin {

    // Static instance
    private static PopolServer instance;

    // Retrieve instance
    public static PopolServer getInstance() {
        return instance;
    }

    // Properties
    private PopolConnector connector;

    // Enable plugin
    @Override
    public void onEnable() {
        // Store instance
        instance = this;

        // Register events
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);

        // Register commands
        getCommand("menu").setExecutor(new MenuCommand());

        // Send status
        sendStatus();
    }

    // Disable plugin
    @Override
    public void onDisable() {
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

    // Send current server status
    public void sendStatus() {
        getConnector().putServer(new APIServer(null, null, null, null, null, null, "online", Bukkit.getOnlinePlayers().size()));
    }

}
