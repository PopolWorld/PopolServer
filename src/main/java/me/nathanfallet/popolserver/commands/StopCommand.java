package me.nathanfallet.popolserver.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public class StopCommand extends Command {

    public StopCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check sender
        if (sender.equals(ProxyServer.getInstance().getConsole())) {
            // Stop server
            ProxyServer.getInstance().stop();
        }
    }

}
