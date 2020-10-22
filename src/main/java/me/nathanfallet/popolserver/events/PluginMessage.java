package me.nathanfallet.popolserver.events;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import me.nathanfallet.popolserver.BungeePopolServer;
import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
                    final String id = in.readUTF();

                    // Get player
                    final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(e.getReceiver().toString());

                    // Fetch from API
                    BungeePopolServer.getInstance().getConnector().getServer(id, new CompletionHandler<APIServer>() {
                        @Override
                        public void completionHandler(APIServer object, APIResponseStatus status) {
                            // Check status and server
                            if (object != null && status == APIResponseStatus.ok) {
                                // Connect player to server
                                player.connect(ProxyServer.getInstance().constructServerInfo(object.id,
                                        new InetSocketAddress("127.0.0.1", object.port), "", false));
                            } else {
                                // Error
                                player.sendMessage(
                                        TextComponent.fromLegacyText("Ce serveur n'existe pas !", ChatColor.RED));
                            }
                        }
                    });
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
