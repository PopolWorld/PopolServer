package me.nathanfallet.popolserver.api;

public class APITeam {

    public Long id;
    public String name;
    public Long money;
    public APIPlayer[] players;
    public APITeamMember role;

    public APITeam(Long id, String name, Long money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }
    
}
