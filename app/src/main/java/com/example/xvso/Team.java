package com.example.xvso;

import androidx.lifecycle.ViewModel;

public class Team extends ViewModel {

    private int currentTeam;

    // team X
    public static final int TEAM_X = 1;
    private int teamNameX;
    private int scoreTeamX;
    private String displayNameX;

    // team O
    public static final int TEAM_O = 2;
    private int teamNameO;
    private int scoreTeamO;
    private String displayNameO;

    // constructor
    public Team(int currentTeam) {
        this.currentTeam = currentTeam;
    }

    // getters and setters
    public int getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(int currentTeam) {

        if (currentTeam != 1 && currentTeam != 2) {
            throw new IllegalArgumentException();
        }
        this.currentTeam = currentTeam;
    }

    public int getTeamNameX() {
        return teamNameX;
    }

    public void setTeamNameX(int teamNameX) {
        this.teamNameX = teamNameX;
    }

    public int getTeamNameO() {
        return teamNameO;
    }

    public void setTeamNameO(int teamNameO) {
        this.teamNameO = teamNameO;
    }

    public int getScoreTeamX() {
        return scoreTeamX;
    }

    public void setScoreTeamX(int scoreTeamX) {
        this.scoreTeamX = scoreTeamX;
    }

    public int getScoreTeamO() {
        return scoreTeamO;
    }

    public void setScoreTeamO(int scoreTeamO) {
        this.scoreTeamO = scoreTeamO;
    }

    public String getDisplayNameX() {
        return displayNameX;
    }

    public void setDisplayNameX(String displayNameX) {
        this.displayNameX = displayNameX;
    }

    public String getDisplayNameO() {
        return displayNameO;
    }

    public void setDisplayNameO(String displayNameO) {
        this.displayNameO = displayNameO;
    }
}
