package com.example.xvso.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;


public class MainActivityViewModel extends ViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();

    // Tracks the score of player X
    private int scorePlayerX = 0;

    // Tracks the score of player O
    private int scorePlayerO = 0;

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

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(LOG_TAG, "ViewModel was destroyed");
    }
}
