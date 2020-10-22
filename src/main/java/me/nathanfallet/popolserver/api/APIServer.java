package me.nathanfallet.popolserver.api;

public class APIServer {

    public String id;
    public String name;
    public String software;
    public Integer port;
    public Integer slot;
    public String icon;
    public String status;
    public Integer players;

    public APIServer(String id, String name, String software, Integer port, Integer slot, String icon, String status,
            Integer players) {
        this.id = id;
        this.name = name;
        this.software = software;
        this.port = port;
        this.slot = slot;
        this.icon = icon;
        this.status = status;
        this.players = players;
    }

}
