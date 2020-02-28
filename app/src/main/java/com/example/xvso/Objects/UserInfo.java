package com.example.xvso.Objects;

public class UserInfo {

    // users : playerX, playerO
    private String playerX;
    private String playerO;
    // users' score: scorePlayerX, scorePlayerO
    private int scorePlayerX;
    private int scorePlayerO;
    // users' status: readyToPlayX, readyToPlayO
    private boolean readyToPlayX;
    private boolean readyToPlayO;

    public String getPlayerX() {
        return playerX;
    }

    public void setPlayerX(String playerX) {
        this.playerX = playerX;
    }

    public String getPlayerO() {
        return playerO;
    }

    public void setPlayerO(String playerO) {
        this.playerO = playerO;
    }

    public int getScorePlayerX() {
        return scorePlayerX;
    }

    public void setScorePlayerX(int scorePlayerX) {
        this.scorePlayerX = scorePlayerX;
    }

    public int getScorePlayerO() {
        return scorePlayerO;
    }

    public void setScorePlayerO(int scorePlayerO) {
        this.scorePlayerO = scorePlayerO;
    }

    public boolean isReadyToPlayX() {
        return readyToPlayX;
    }

    public void setReadyToPlayX(boolean readyToPlayX) {
        this.readyToPlayX = readyToPlayX;
    }

    public boolean isReadyToPlayO() {
        return readyToPlayO;
    }

    public void setReadyToPlayO(boolean readyToPlayO) {
        this.readyToPlayO = readyToPlayO;
    }
}
