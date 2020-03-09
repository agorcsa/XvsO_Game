package com.example.xvso.Objects;

public class Game {

    private UserInfo userInfo;

    private Board board;

    private User host;
    private User guest;

    // game status
    public static final int STATUS_WAITING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_FINISHED = 2;

    private int gameStatus;


    // empty constructor
    public Game() {

    }

    // constructor
    public Game(Board board, int gameStatus, User host, User guest) {

        this.board = board;
        this.gameStatus = gameStatus;
        this.host = host;
        this.guest = guest;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }
}
