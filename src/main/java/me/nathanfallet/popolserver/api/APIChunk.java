package me.nathanfallet.popolserver.api;

public class APIChunk {

    public Long id;
    public Long x;
    public Long z;
    public Long teamId;

    public APIChunk(Long id, Long x, Long z, Long teamId) {
        this.id = id;
        this.x = x;
        this.z = z;
        this.teamId = teamId;
    }
    
}
