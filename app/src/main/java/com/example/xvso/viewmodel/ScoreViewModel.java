package com.example.xvso.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.ViewModel;

import com.example.xvso.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoreViewModel extends ViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();
    // if variable isX = 2, the cell stores a "O" not an "X"
    private int isX = 1;
    private boolean isWinner;
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

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

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

    public boolean checkRows() {
        if (mCellIndex.get(0) == isX && mCellIndex.get(1) == isX && mCellIndex.get(2) == isX) {
            isWinner = true;
            //activityBinding.topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(3) == isX && mCellIndex.get(4) == isX && mCellIndex.get(5) == isX) {
            isWinner = true;
            //activityBinding.centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(6) == isX && mCellIndex.get(7) == isX && mCellIndex.get(8) == isX) {
            isWinner = true;
            //activityBinding.bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isWinner = true;
            return false;
        }
    }


    public boolean checkColumns() {
        if (mCellIndex.get(0) == isX && mCellIndex.get(3) == isX && mCellIndex.get(6) == isX) {
            isWinner = true;
            //activityBinding.leftVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(1) == isX && mCellIndex.get(4) == isX && mCellIndex.get(7) == isX) {
            isWinner = true;
            //activityBinding.centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == isX && mCellIndex.get(5) == isX && mCellIndex.get(8) == isX) {
            isWinner = true;
            //activityBinding.rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkDiagonals() {
        if (mCellIndex.get(0) == isX && mCellIndex.get(4) == isX && mCellIndex.get(8) == isX) {
            isWinner = true;
            // activityBinding.leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == isX && mCellIndex.get(4) == isX && mCellIndex.get(6) == isX) {
            isWinner = true;
            //  activityBinding.rightLeftDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isWinner = true;
            return false;
        }
    }

    public boolean fullBoard() {
        // iterate in the whole list and read the mCellStatus of the mCellIndex

        for (int i = 0; i < mCellIndex.size(); i++) {
            if (mCellIndex.get(i) == 0) {
                return false;
            }
        }
        return true;
    }

    public void dropIn(View view) {

        if (checkForWin()) {
            // There is a winner
        } else if (fullBoard()) {
            // Not a winner but not more moves - it's a draw
        } else {
            // keep playing
            ImageView counter = (ImageView) view;

            counter.setTranslationY(-1000f);

            counter.animate().translationYBy(1000f).setDuration(300);

            setTag(Integer.parseInt((String) counter.getTag()));
            //Toast.makeText(this, "Tag: " + mScoreViewModel.getTag(), Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked tag: " + getTag());

            if (isX == 1 && !checkForWin()) {
                counter.setImageResource(R.drawable.ic_cross);
                mCellIndex.set(getTag(), getIsX());
                Log.i(LOG_TAG, "mCellIndex: " + getCellIndex());
                setIsX(2);
                view.setClickable(false);
            } else if (getIsX() == 2 && !checkForWin()) {
                counter.setImageResource(R.drawable.ic_zero);
                getCellIndex().set(getTag(), getIsX());
                setIsX(1);
                view.setClickable(false);
            }
        }
    }


    public boolean checkForWin() {
        if (checkRows() || checkColumns() || checkDiagonals()) {
            setGameOver(true);
            //setClickableFalse();
            return true;
        } else {
            setGameOver(false);
            return false;
        }
    }

    public boolean resetBoard() {

        setCellIndex(new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0)));
        return true;
    }

    public boolean initializePlayers() {

        counterPlayer1 = 0;
        counterPlayer2 = 0;

        return true;
    }

    public void readFromViewModelX() {
        setCounterPlayer1(getScorePlayerX());
    }

    public void writeToViewModelX() {
        setScorePlayerX(getCounterPlayer1());
    }

    public void readFromViewModelO() {
        setCounterPlayer2(getScorePlayerO());
    }

    public void writeToViewModelO() {
        setScorePlayerO(getCounterPlayer2());
    }

    public void viewModelX() {
        readFromViewModelX();
        counterPlayer1++;
        writeToViewModelX();
    }

    public void viewModelO() {
        readFromViewModelX();
        counterPlayer2++;
        writeToViewModelO();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(LOG_TAG, "ViewModel was destroyed");
    }
}
