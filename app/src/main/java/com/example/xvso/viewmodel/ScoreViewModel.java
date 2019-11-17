package com.example.xvso.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoreViewModel extends ViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();
    // if variable isX = 2, the cell stores a "O" not an "X"
    private int isX = 1;
    private boolean isXWinner;
    private int counterPlayer1 = 0;
    private int counterPlayer2 = 0;
    // Tracks the score of player X
    private int scorePlayerX = 0;
    // Tracks the score of player O
    private int scorePlayerO = 0;
    // takes values from 0 -> 8
    // represents the tag of each cell of the grid
    // (0, 1, 2)
    // (3, 4, 5)
    // (6, 7, 8)
    private int tag;
    private String displayName;
    private ArrayList<Integer> mCellIndex = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
    private boolean gameOver;

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public ArrayList<Integer> getCellIndex() {
        return mCellIndex;
    }

    public void setCellIndex(ArrayList<Integer> mCellIndex) {
        this.mCellIndex = mCellIndex;
    }

    public int getCounterPlayer1() {
        return counterPlayer1;
    }

    public void setCounterPlayer1(int counterPlayer1) {
        this.counterPlayer1 = counterPlayer1;
    }

    public int getCounterPlayer2() {
        return counterPlayer2;
    }

    public void setCounterPlayer2(int counterPlayer2) {
        this.counterPlayer2 = counterPlayer2;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    //getter
    public boolean XWinner() {
        return this.isXWinner;
    }

    public void setXWinner(boolean isXWinner) {
        this.isXWinner = isXWinner;
    }

    public int getIsX() {
        return isX;
    }

    public void setIsX(int isX) {
        this.isX = isX;
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

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(LOG_TAG, "ViewModel was destroyed");
    }
}
