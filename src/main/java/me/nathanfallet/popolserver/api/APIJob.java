package me.nathanfallet.popolserver.api;

public class APIJob {

    public Long id;
    public String job;
    public String playerUuid;
    public Long experience;
    public Boolean active;

    public APIJob(Long id, String job, String playerUuid, Long experience, Boolean active) {
        this.id = id;
        this.job = job;
        this.playerUuid = playerUuid;
        this.experience = experience;
        this.active = active;
    }

}
