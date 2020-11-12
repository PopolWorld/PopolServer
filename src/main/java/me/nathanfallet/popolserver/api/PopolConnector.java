package me.nathanfallet.popolserver.api;

import com.google.gson.GsonBuilder;

import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;

public class PopolConnector {

    // Properties
    private String id;
    private String token;
    private String host;

    // Cached current server
    private APIServer cached;

    // Constructor
    public PopolConnector(String id, String token, String host) {
        this.id = id;
        this.token = token;
        this.host = host;
    }

    // Retrieve server from cache
    public APIServer getFromCache() {
        return cached;
    }

    /**
     * Servers
     */

    // Get server list
    public void getServer(CompletionHandler<APIServer[]> completionHandler) {
        // Send GET to server
        new APIRequest<APIServer[]>("GET", host, "/server/").execute(APIServer[].class, completionHandler);
    }

    // Get a server by id
    public void getServer(String serverId, CompletionHandler<APIServer> completionHandler) {
        // Send GET to server/:id
        new APIRequest<APIServer>("GET", host, "/server/" + serverId).execute(APIServer.class, completionHandler);
    }

    // Update current server
    public void putServer(APIServer server) {
        // Send PUT to server/:id
        new APIRequest<APIServer>("PUT", host, "/server/" + id).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(server))
                .execute(APIServer.class, new CompletionHandler<APIServer>() {
                    @Override
                    public void completionHandler(APIServer object, APIResponseStatus status) {
                        // Update cache
                        cached = object;
                    }
                });
    }

    /**
     * Players
     */

    // Get a player by uuid
    public void getPlayer(String playerUUID, CompletionHandler<APIPlayer> completionHandler) {
        // Send GET to player/:uuid
        new APIRequest<APIPlayer>("GET", host, "/player/" + playerUUID).execute(APIPlayer.class, completionHandler);
    }

    // Update a player
    public void putPlayer(APIPlayer player, CompletionHandler<APIPlayer> completionHandler) {
        // Send PUT to player/:uuid
        new APIRequest<APIPlayer>("PUT", host, "/player/" + player.uuid).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(player)).execute(APIPlayer.class, completionHandler);
    }

    /**
     * Teams
     */

    // Get team leaderboard
    public void getTeamLeaderboard(int limit, CompletionHandler<APITeam[]> completionHandler) {
        // Send GET to team/leaderboard/:limit
        new APIRequest<APITeam[]>("GET", host, "/team/leaderboard/" + limit).execute(APITeam[].class,
                completionHandler);
    }

    // Get a team
    public void getTeam(String teamId, CompletionHandler<APITeam> completionHandler) {
        // Send GET to team/:id
        new APIRequest<APITeam>("GET", host, "/team/" + teamId).execute(APITeam.class, completionHandler);
    }

    // Update a team
    public void putTeam(APITeam team, CompletionHandler<APITeam> completionHandler) {
        // Send PUT to team/:id
        new APIRequest<APITeam>("PUT", host, "/team/" + team.id).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(team)).execute(APITeam.class, completionHandler);
    }

    // Delete a team
    public void deleteTeam(String teamId, CompletionHandler<APIMessage> completionHandler) {
        // Send DELETE to team/:id
        new APIRequest<APIMessage>("DELETE", host, "/team/" + teamId).withHeader("token", token)
                .execute(APIMessage.class, completionHandler);
    }

    // Create a team
    public void postTeam(APITeamCreation team, CompletionHandler<APITeam> completionHandler) {
        // Send POST to team
        new APIRequest<APITeam>("POST", host, "/team/").withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(team)).execute(APITeam.class, completionHandler);
    }

    // Add member to a team
    public void postTeamPlayer(String teamId, String playerUuid, String role,
            CompletionHandler<APITeam> completionHandler) {
        // Send POST to team/:id/:uuid
        new APIRequest<APITeam>("POST", host, "/team/" + teamId + "/" + playerUuid).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(new APITeamMember(role)))
                .execute(APITeam.class, completionHandler);
    }

    // Remove member from a team
    public void deleteTeamPlayer(String teamId, String playerUuid, CompletionHandler<APITeam> completionHandler) {
        // Send DELETE to team/:id/:uuid
        new APIRequest<APITeam>("DELETE", host, "/team/" + teamId + "/" + playerUuid).withHeader("token", token)
                .execute(APITeam.class, completionHandler);
    }

}
