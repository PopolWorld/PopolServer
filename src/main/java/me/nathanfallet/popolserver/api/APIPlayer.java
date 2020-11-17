package me.nathanfallet.popolserver.api;

public class APIPlayer {

    public String uuid;
    public String name;
    public Long money;
    public Boolean moderator;
    public Boolean administrator;
    public APITeam[] teams;
    public APITeamMember team_member;

    public APIPlayer(String uuid, String name, Long money, Boolean moderator, Boolean administrator) {
        this.uuid = uuid;
        this.name = name;
        this.money = money;
        this.moderator = moderator;
        this.administrator = administrator;
    }    

}
