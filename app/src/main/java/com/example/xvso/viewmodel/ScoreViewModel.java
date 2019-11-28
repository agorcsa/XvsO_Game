package com.example.xvso.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xvso.Team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoreViewModel extends ViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();


    private MutableLiveData<Team> teamX;

    private MutableLiveData<Team> teamO;

    private MutableLiveData<Team> currentTeam;

    // constructor
    // will be called when MainActivity starts
    public ScoreViewModel() {

        teamX.setValue(new Team(Team.TEAM_X));
        teamO.setValue(new Team(Team.TEAM_O));
        currentTeam = teamX.getValue();
    }

    private final MutableLiveData<Boolean> topHorizontalLine = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> centerHorizontal = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> bottomHorizontal = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> leftVertical = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> centerVertical = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> rightVertical = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> leftRightDiagonal = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> rightLeftDiagonal = new MutableLiveData<>(false);


    public MutableLiveData<Boolean> getTopHorizontalLine() {
        return topHorizontalLine;
    }

    public MutableLiveData<Boolean> getCenterHorizontal() {
        return centerHorizontal;
    }

    public MutableLiveData<Boolean> getBottomHorizontal() {
        return bottomHorizontal;
    }

    public MutableLiveData<Boolean> getLeftVertical() {
        return leftVertical;
    }

    public MutableLiveData<Boolean> getCenterVertical() {
        return centerVertical;
    }

    public MutableLiveData<Boolean> getRightVertical() {
        return rightVertical;
    }

    public MutableLiveData<Boolean> getLeftRightDiagonal() {
        return leftRightDiagonal;
    }

    public MutableLiveData<Boolean> getRightLeftDiagonal() {
        return rightLeftDiagonal;
    }

    private boolean isWinner;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }


    public void updateDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            currentTeam.setDisplayNameX(user.getDisplayName());

            if (user.getDisplayName() != null && user.getDisplayName().isEmpty()) {
                //player1Text.setText(user.getDisplayName() + " X:");
            } else {
                String userString = user.getEmail().substring(0, user.getEmail().indexOf("@"));
                //player1Text.setText(userString + " X:");
            }
            //updateCounters();
            //preserveBoard();
        }
    }

    public void togglePlayer() {
        if (currentTeam == teamO) {
                currentTeam = teamX;
        } else {
            currentTeam = teamO;
        }
    }


    public boolean checkRows() {

        if (mCellIndex.get(0) == currentTeam.getValue() && mCellIndex.get(1) == currentTeam.getValue() && mCellIndex.get(2) == currentTeam.getValue()) {
            isWinner = true;
            topHorizontalLine.setValue(true);
            return true;
        } else if (mCellIndex.get(3) == currentTeam.getValue() && mCellIndex.get(4) == currentTeam.getValue() && mCellIndex.get(5) == currentTeam.getValue()) {
            isWinner = true;
            centerHorizontal.setValue(true);
            return true;
        } else if (mCellIndex.get(6) == currentTeam.getValue() && mCellIndex.get(7) == currentTeam.getValue() && mCellIndex.get(8) == currentTeam.getValue()) {
            isWinner = true;
            bottomHorizontal.setValue(true);
            return true;
        } else {
            isWinner = false;
            return false;
        }
    }

    public boolean checkColumns() {
        if (mCellIndex.get(0) == currentTeam.getValue() && mCellIndex.get(3) == currentTeam.getValue() && mCellIndex.get(6) == currentTeam.getValue()) {
            isWinner = true;
            leftVertical.setValue(true);
            return true;
        } else if (mCellIndex.get(1) == currentTeam.getValue()) && mCellIndex.get(4) == currentTeam.getValue() && mCellIndex.get(7) == currentTeam.getValue()) {
            isWinner = true;
            centerVertical.setValue(true);
            return true;
        } else if (mCellIndex.get(2) == currentTeam.getValue() && mCellIndex.get(5) == currentTeam.getValue() && mCellIndex.get(8) == currentTeam.getValue()) {
            isWinner = true;
            rightVertical.setValue(true);
            return true;
        } else {
            isWinner = false;
            return false;
        }
    }

    public boolean checkDiagonals() {
        if (mCellIndex.get(0) == currentTeam.getValue() && mCellIndex.get(4) == currentTeam.getValue()) && mCellIndex.get(8) == currentTeam.getValue()) {
            isWinner = true;
            leftRightDiagonal.setValue(true);
            return true;
        } else if (mCellIndex.get(2) == currentTeam.getValue() && mCellIndex.get(4) == currentTeam.getValue() && mCellIndex.get(6) == currentTeam.getValue()) {
            isWinner = true;
            rightLeftDiagonal.setValue(true);
            return true;
        } else {
            isWinner = false;
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

    public boolean initializePlayers() {

        teamX.setValue(new Team());
        teamO.setValue(new Team());

        return true;
    }

    public void viewModel() {
        // read teamScore from VM
        currentTeam.getValue();

        // increment teamScore with + 1
        // no matter if teamX or teamO
        currentTeam.incrementScore();

        // write the the previous incremented teamScore back to the VM
        currentTeam.setValue();
    }

    public void incrementScore(){
        //getTeamScore() = getTeamScore + 1;
    }
}
