package com.example.xvso.Objects;

public class Game {

    // game status constants
    public static final int STATUS_WAITING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_FINISHED = 2;

    private Board board;
    private User host;
    private User guest;
    private int status;

    private int picture;
    private String gameNumber;
    private String userName;

    private String key;

    // empty constructor
    public Game() {

    }

    public Game(int picture, String gameNumber, String userName) {
        this.picture = picture;
        this.gameNumber = gameNumber;
        this.userName = userName;
    }

    // constructor
    public Game(Board board, User host, User guest, int status, int picture, String gameNumber, String userName, String key) {
        this.board = board;
        this.host = host;
        this.guest = guest;
        this.status = status;
        this.picture = picture;
        this.gameNumber = gameNumber;
        this.userName = userName;
        this.key = key;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(String gameNumber) {
        this.gameNumber = gameNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}


