package com.example.xvso.viewmodel;

import androidx.lifecycle.ViewModel;

public class ScoreViewModel extends ViewModel {

    // Tracks the score of player X
    private int scorePlayerX;

    // Tracks the score of player O
    private int scorePlayerO;

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
}
