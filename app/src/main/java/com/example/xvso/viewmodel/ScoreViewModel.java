package com.example.xvso.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xvso.Team;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoreViewModel extends ViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private final MutableLiveData<Boolean> topHorizontalLine = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> centerHorizontal = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> bottomHorizontal = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> leftVertical = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> centerVertical = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> rightVertical = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> leftRightDiagonal = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> rightLeftDiagonal = new MutableLiveData<>(false);
    private MutableLiveData<Team> teamX = new MutableLiveData<>();
    private MutableLiveData<Team> teamO = new MutableLiveData<>();
    private Team currentTeam;
    // represents the tag of each cell of the grid
    // (0, 1, 2)
    // (3, 4, 5)
    // (6, 7, 8)
    private int tag;
    private String displayName;
    private ArrayList<Integer> mCellIndex = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
    private boolean gameOver;
    // constructor
    // will be called when MainActivity starts
    public ScoreViewModel() {

        teamX.setValue(new Team(Team.TEAM_X));
        teamO.setValue(new Team(Team.TEAM_O));
        currentTeam = teamX.getValue();
    }

    public MutableLiveData<Team> getTeamX() {
        return teamX;
    }

    public void setTeamX(MutableLiveData<Team> teamX) {
        this.teamX = teamX;
    }

    public MutableLiveData<Team> getTeamO() {
        return teamO;
    }

    public void setTeamO(MutableLiveData<Team> teamO) {
        this.teamO = teamO;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

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


    /* public void updateDisplayName() {
         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

         if (user != null) {
             currentTeam.setDisplayNameX(user.getDisplayName());

             if (user.getDisplayName() != null && user.getDisplayName().isEmpty()) {
                 //player1Text.setText(user.getDisplayName() + " X:");
             } else {
                 String userString = user.getEmail().substring(0, user.getEmail().indexOf("@"));
                 //player1Text.setText(userString + " X:");
             }
             updateCounters();
             preserveBoard();
         }
     }
 */
    public void togglePlayer() {
        if (currentTeam == teamO.getValue()) {
            currentTeam = teamX.getValue();
        } else {
            currentTeam = teamO.getValue();
        }
    }


    public boolean checkRows() {

        int team = currentTeam.getTeamType();

        if (mCellIndex.get(0) == team && mCellIndex.get(1) == team && mCellIndex.get(2) == team) {
            topHorizontalLine.setValue(true);
            return true;
        } else if (mCellIndex.get(3) == team && mCellIndex.get(4) == team && mCellIndex.get(5) == team) {
            centerHorizontal.setValue(true);
            return true;
        } else if (mCellIndex.get(6) == team && mCellIndex.get(7) == team && mCellIndex.get(8) == team) {
            bottomHorizontal.setValue(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkColumns() {

        int team = currentTeam.getTeamType();

        if (mCellIndex.get(0) == team && mCellIndex.get(3) == team && mCellIndex.get(6) == team) {
            leftVertical.setValue(true);
            return true;
        } else if (mCellIndex.get(1) == team && mCellIndex.get(4) == team && mCellIndex.get(7) == team) {
            centerVertical.setValue(true);
            return true;
        } else if (mCellIndex.get(2) == team && mCellIndex.get(5) == team && mCellIndex.get(8) == team) {
            rightVertical.setValue(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkDiagonals() {

        int team = currentTeam.getTeamType();

        if (mCellIndex.get(0) == team && mCellIndex.get(4) == team && mCellIndex.get(8) == team) {
            leftRightDiagonal.setValue(true);
            return true;
        } else if (mCellIndex.get(2) == team && mCellIndex.get(4) == team && mCellIndex.get(6) == team) {
            rightLeftDiagonal.setValue(true);
            return true;
        } else {
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


    public void updateScore() {
        currentTeam.incrementScore();

        if (currentTeam.getTeamType() == Team.TEAM_X) {
            teamX.setValue(currentTeam);
        } else {
            teamO.setValue(currentTeam);
        }
    }
}
