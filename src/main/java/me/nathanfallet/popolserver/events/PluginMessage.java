package me.nathanfallet.popolserver.events;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessage implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        // Check channel tag
        if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            // Read channel
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
                String channel = in.readUTF();

                // Check channel
                if (channel.equals("PopolConnect")) {
                    // Read server id
                    String id = in.readUTF();

                    // Fetch from API
                    // TODO: Implement using APIRequest

                    // Connect player to server
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(e.getReceiver().toString());
                    player.connect(ProxyServer.getInstance().constructServerInfo(id, new InetSocketAddress("ip", 25565),
                            "", false));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
