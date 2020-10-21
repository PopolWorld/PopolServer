package me.nathanfallet.popolserver;

import me.nathanfallet.popolserver.commands.StopCommand;
import me.nathanfallet.popolserver.events.PluginMessage;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePopolServer extends Plugin {

    @Override
    public void onEnable() {
        // Enable plugin
        getLogger().info("Enabling BungeePopolServer...");

        // Register events
        getProxy().getPluginManager().registerListener(this, new PluginMessage());

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new StopCommand("stop"));
    }
    
}
