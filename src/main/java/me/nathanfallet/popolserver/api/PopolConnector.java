package me.nathanfallet.popolserver.api;

import com.google.gson.GsonBuilder;

import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;

public class PopolConnector {

    private String id;
    private String token;

    public PopolConnector(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public void getServer(String serverId, CompletionHandler<APIServer> completionHandler) {
        // Send GET to server
        new APIRequest<APIServer>("GET", "/server/" + serverId).execute(APIServer.class, completionHandler);
    }

    public void putServer(APIServer server) {
        // Send PUT to server
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
