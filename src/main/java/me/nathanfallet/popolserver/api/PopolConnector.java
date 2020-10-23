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

}
