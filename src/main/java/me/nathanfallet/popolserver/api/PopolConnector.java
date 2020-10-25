package me.nathanfallet.popolserver.api;

import com.google.gson.GsonBuilder;

import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;

public class PopolConnector {

    // Properties
    private String id;
    private String token;

    // Constructor
    public PopolConnector(String id, String token) {
        this.id = id;
        this.token = token;
    }

    /**
     * Servers
     */

    // Get server list
    public void getServer(CompletionHandler<APIServer[]> completionHandler) {
        // Send GET to server
        new APIRequest<APIServer[]>("GET", "/server/").execute(APIServer[].class, completionHandler);
    }

    // Get a server by id
    public void getServer(String serverId, CompletionHandler<APIServer> completionHandler) {
        // Send GET to server/:id
        new APIRequest<APIServer>("GET", "/server/" + serverId).execute(APIServer.class, completionHandler);
    }

    // Update current server
    public void putServer(APIServer server) {
        // Send PUT to server/:id
        new APIRequest<APIServer>("PUT", "/server/" + id).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(server))
                .execute(APIServer.class, new CompletionHandler<APIServer>() {
                    @Override
                    public void completionHandler(APIServer object, APIResponseStatus status) {
                        // Awesome, we don't need to do anything here
                    }
                });
    }

    /**
     * Players
     */

    // Get a player by uuid
    public void getPlayer(String playerUUID, CompletionHandler<APIPlayer> completionHandler) {
        // Send GET to player/:uuid
        new APIRequest<APIPlayer>("GET", "/player/" + playerUUID).execute(APIPlayer.class, completionHandler);
    }

    // Update a player
    public void putPlayer(APIPlayer player, CompletionHandler<APIPlayer> completionHandler) {
        // Send PUT to player/:uuid
        new APIRequest<APIPlayer>("PUT", "/player/" + player.uuid).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(player)).execute(APIPlayer.class, completionHandler);
    }

}
