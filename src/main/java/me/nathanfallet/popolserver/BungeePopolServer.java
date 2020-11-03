package me.nathanfallet.popolserver;

import java.io.File;

import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.PopolConnector;
import me.nathanfallet.popolserver.commands.StopCommand;
import me.nathanfallet.popolserver.events.PlayerJoin;
import me.nathanfallet.popolserver.events.PlayerQuit;
import me.nathanfallet.popolserver.events.PluginMessage;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeePopolServer extends Plugin {

    // Static instance
    private static BungeePopolServer instance;

    // Retrieve instance
    public static BungeePopolServer getInstance() {
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
        getProxy().getPluginManager().registerListener(this, new PlayerJoin());
        getProxy().getPluginManager().registerListener(this, new PlayerQuit());
        getProxy().getPluginManager().registerListener(this, new PluginMessage());

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new StopCommand("stop"));

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
                // Get config
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                        .load(new File(getDataFolder(), "config.yml"));

                // Create connector with config
                connector = new PopolConnector(config.getString("id"), config.getString("token"),
                        config.getString("host"));
            } catch (Exception e) {
                // Error
                e.printStackTrace();

                // Stop server
                getProxy().stop();
            }
        }

        // Return connector
        return connector;
    }

    // Send current server status
    public void sendStatus() {
        getConnector()
                .putServer(new APIServer(null, null, null, null, null, null, "online", getProxy().getOnlineCount()));
    }

}
