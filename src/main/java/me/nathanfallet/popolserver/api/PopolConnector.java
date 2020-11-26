package me.nathanfallet.popolserver.api;

import com.google.gson.GsonBuilder;

import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;

public class PopolConnector {

    // Properties
    private String id;
    private String token;
    private APIConfiguration configuration;

    // Cached current server
    private APIServer cached;

    // Constructor
    public PopolConnector(String id, String token, APIConfiguration configuration) {
        this.id = id;
        this.token = token;
        this.configuration = configuration;
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
        new APIRequest<APIServer[]>("GET", "/server/", configuration).execute(APIServer[].class, completionHandler);
    }

    // Get a server by id
    public void getServer(String serverId, CompletionHandler<APIServer> completionHandler) {
        // Send GET to server/:id
        new APIRequest<APIServer>("GET", "/server/" + serverId, configuration).execute(APIServer.class,
                completionHandler);
    }

    // Update current server
    public void putServer(APIServer server) {
        // Send PUT to server/:id
        new APIRequest<APIServer>("PUT", "/server/" + id, configuration).withHeader("token", token)
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
        new APIRequest<APIPlayer>("GET", "/player/" + playerUUID, configuration).execute(APIPlayer.class,
                completionHandler);
    }

    // Update a player
    public void putPlayer(APIPlayer player, CompletionHandler<APIPlayer> completionHandler) {
        // Send PUT to player/:uuid
        new APIRequest<APIPlayer>("PUT", "/player/" + player.uuid, configuration).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(player)).execute(APIPlayer.class, completionHandler);
    }

    /**
     * Teams
     */

    // Get team leaderboard
    public void getTeamLeaderboard(int limit, CompletionHandler<APITeam[]> completionHandler) {
        // Send GET to team/leaderboard/:limit
        new APIRequest<APITeam[]>("GET", "/team/leaderboard/" + limit, configuration).execute(APITeam[].class,
                completionHandler);
    }

    // Get a team
    public void getTeam(Long teamId, CompletionHandler<APITeam> completionHandler) {
        // Send GET to team/:id
        new APIRequest<APITeam>("GET", "/team/" + teamId, configuration).execute(APITeam.class, completionHandler);
    }

    // Update a team
    public void putTeam(APITeam team, CompletionHandler<APITeam> completionHandler) {
        // Send PUT to team/:id
        new APIRequest<APITeam>("PUT", "/team/" + team.id, configuration).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(team)).execute(APITeam.class, completionHandler);
    }

    // Delete a team
    public void deleteTeam(Long teamId, CompletionHandler<APIMessage> completionHandler) {
        // Send DELETE to team/:id
        new APIRequest<APIMessage>("DELETE", "/team/" + teamId, configuration).withHeader("token", token)
                .execute(APIMessage.class, completionHandler);
    }

    // Create a team
    public void postTeam(APITeamCreation team, CompletionHandler<APITeam> completionHandler) {
        // Send POST to team
        new APIRequest<APITeam>("POST", "/team/", configuration).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(team)).execute(APITeam.class, completionHandler);
    }

    // Add member to a team
    public void postTeamPlayer(Long teamId, String playerUuid, String role,
            CompletionHandler<APITeam> completionHandler) {
        // Send POST to team/:id/:uuid
        new APIRequest<APITeam>("POST", "/team/" + teamId + "/" + playerUuid, configuration).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(new APITeamMember(role)))
                .execute(APITeam.class, completionHandler);
    }

    // Remove member from a team
    public void deleteTeamPlayer(Long teamId, String playerUuid, CompletionHandler<APITeam> completionHandler) {
        // Send DELETE to team/:id/:uuid
        new APIRequest<APITeam>("DELETE", "/team/" + teamId + "/" + playerUuid, configuration)
                .withHeader("token", token).execute(APITeam.class, completionHandler);
    }

    // Edit team member
    public void putTeamPlayer(Long teamId, String playerUuid, String role,
            CompletionHandler<APITeam> completionHandler) {
        // Send PUT to team/:id/:uuid
        new APIRequest<APITeam>("PUT", "/team/" + teamId + "/" + playerUuid, configuration).withHeader("token", token)
                .withBody(new GsonBuilder().create().toJson(new APITeamMember(role)))
                .execute(APITeam.class, completionHandler);
    }

    /**
     * Jobs
     */

    // Get a job for a player
    public void getJob(String playerUUID, String job, CompletionHandler<APIJob> completionHandler) {
        // Send GET to job/:uuid/:job
        new APIRequest<APIJob>("GET", "/job/" + playerUUID + "/" + job, configuration).execute(APIJob.class,
                completionHandler);
    }

    // Update a job for a player
    public void putJob(APIJob job, CompletionHandler<APIJob> completionHandler) {
        // Send PUT to job/:uuid/:job
        new APIRequest<APIJob>("PUT", "/job/" + job.playerUuid + "/" + job.job, configuration)
                .withHeader("token", token).withBody(new GsonBuilder().create().toJson(job))
                .execute(APIJob.class, completionHandler);
    }

    // Create a job for a player
    public void postJob(String playerUUID, String job, CompletionHandler<APIJob> completionHandler) {
        // Send POST to job/:uuid/:job
        new APIRequest<APIJob>("POST", "/job/" + playerUUID + "/" + job, configuration).withHeader("token", token)
                .execute(APIJob.class, completionHandler);
    }

}
