package com.example.xvso.viewmodel;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.xvso.Team;
import com.example.xvso.User;
import com.example.xvso.firebaseutils.FirebaseQueryLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private ArrayList<Integer> board = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
    private MutableLiveData<ArrayList<Integer>> boardLiveData = new MutableLiveData<>();
    private boolean gameOver;
    private DatabaseReference query;
    private LiveData<User> userLiveData;
    private FirebaseAuth auth;

    // constructor
    // will be called when MainActivity starts
    public ScoreViewModel() {

        teamX.setValue(new Team(Team.TEAM_X));
        teamO.setValue(new Team(Team.TEAM_O));
        currentTeam = teamX.getValue();

        auth = FirebaseAuth.getInstance();

        if (auth.getUid() != null) {
            query = FirebaseDatabase.getInstance().getReference("users").child((auth.getUid()));
        }

        FirebaseQueryLiveData resultLiveData = new FirebaseQueryLiveData(query);
        userLiveData = Transformations.map(resultLiveData, (androidx.arch.core.util.Function<DataSnapshot, User>) new Deserializer());
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
        return board;
    }

    public void setCellIndex(ArrayList<Integer> mCellIndex) {
        this.board = mCellIndex;
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

        if (board.get(0) == team && board.get(1) == team && board.get(2) == team) {
            topHorizontalLine.setValue(true);
            return true;
        } else if (board.get(3) == team && board.get(4) == team && board.get(5) == team) {
            centerHorizontal.setValue(true);
            return true;
        } else if (board.get(6) == team && board.get(7) == team && board.get(8) == team) {
            bottomHorizontal.setValue(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkColumns() {

        int team = currentTeam.getTeamType();

        if (board.get(0) == team && board.get(3) == team && board.get(6) == team) {
            leftVertical.setValue(true);
            return true;
        } else if (board.get(1) == team && board.get(4) == team && board.get(7) == team) {
            centerVertical.setValue(true);
            return true;
        } else if (board.get(2) == team && board.get(5) == team && board.get(8) == team) {
            rightVertical.setValue(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkDiagonals() {

        int team = currentTeam.getTeamType();

        if (board.get(0) == team && board.get(4) == team && board.get(8) == team) {
            leftRightDiagonal.setValue(true);
            return true;
        } else if (board.get(2) == team && board.get(4) == team && board.get(6) == team) {
            rightLeftDiagonal.setValue(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean fullBoard() {
        // iterate in the whole list and read the mCellStatus of the mCellIndex

        for (int i = 0; i < board.size(); i++) {
            if (board.get(i) == 0) {
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

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void setUserLiveData(LiveData<User> userLiveData) {
        this.userLiveData = userLiveData;
    }

    public MutableLiveData<ArrayList<Integer>> getBoardLiveData() {
        return boardLiveData;
    }

    public void setBoardLiveData(MutableLiveData<ArrayList<Integer>> boardLiveData) {
        this.boardLiveData = boardLiveData;
    }

    public void play(int position) {
        // set the current team for the specified position on the board
        // we can have a boardLiveData and call boardLiveData.setValue(board),

        // sets the current team for the specified position on the board
        int teamType = currentTeam.getTeamType();

        if (teamType == 1) {
            board.set(position, Team.TEAM_X);
            boardLiveData.setValue(board);
        } else if (teamType == 2) {
            board.set(position, Team.TEAM_O);
            boardLiveData.setValue(board);
        }
        // so we can use this boardLiveData in the XML to update the value for each cell
        // Don't switch the player here !! Use togglePlayer() instead
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class Deserializer implements Function<DataSnapshot, User> {
        @Override
        public User apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(User.class);
        }
    }
}
