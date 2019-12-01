package com.example.xvso;

public class Team {

    public static final int TEAM_X = 1;
    public static final int TEAM_O = 2;

    private int teamType;
    private int teamScore;
    private String displayName;

    // empty constructor
    public Team() {
    }

    // constructor
    public Team(int teamType) {
        this.teamType = teamType;
    }

    // getters and setters for the generic variables
    public int getTeamType() {
        return teamType;
    }

    public void setTeamType(int teamType) {
        this.teamType = teamType;
    }

    public int getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(int teamScore) {
        this.teamScore = teamScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void incrementScore(){
        teamScore++;
    }
}
