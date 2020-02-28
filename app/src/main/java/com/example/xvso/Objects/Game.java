package com.example.xvso.Objects;

public class Game {

    private UserInfo userInfo;
    private Board board;
    private String[] status = new String[] { "Waiting", "Playing", "Finished" };

    // empty constructor
    public Game() {

    }

    // constructor
    public Game(UserInfo userInfo, Board board, String[] status) {

        this.userInfo = userInfo;
        this.board = board;
        this.status = status;
    }
}
