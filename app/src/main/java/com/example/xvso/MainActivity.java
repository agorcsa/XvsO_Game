package com.example.xvso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.xvso.databinding.ActivityMainBinding;
import com.example.xvso.firebase.BaseActivity;
import com.example.xvso.firebase.ProfileActivity;
import com.example.xvso.viewmodel.MainActivityViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends BaseActivity {

    public static final String LOG_TAG = "MainActivity";

    // represents the tag of each cell of the grid
    // (0, 1, 2)
    // (3, 4, 5)
    // (6, 7, 8)
    public int tag;

    // if variable isX = 2, the cell stores a "O" not an "X"
    public int isX = 1;

    public boolean isXWinner;
    public MainActivityViewModel mScoreViewModel;
    ArrayList<Integer> mCellIndex = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
    // for log out button
    ActivityMainBinding activityBinding;
    // keeps track of the score of both players
    private int counterPlayer1;
    private int counterPlayer2;
    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mScoreViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
    }

    protected void onResume() {
        super.onResume();

        if (getFirebaseUser() != null) {
            displayName = getFirebaseUser().getDisplayName();

            if (!displayName.isEmpty()) {
                activityBinding.player1Text.setText(displayName + " X:");
            } else {
                String userString = getFirebaseUser().getEmail().substring(0, getFirebaseUser().getEmail().indexOf("@"));
                activityBinding.player1Text.setText(userString + " X:");
            }

            //initializePlayers();
            updateCounters();
        }
    }


    public void dropIn(View view) {

        ImageView counter = (ImageView) view;

        counter.setTranslationY(-1000f);

        counter.animate().translationYBy(1000f).setDuration(300);

        tag = Integer.parseInt((String) counter.getTag());

        if (isX == 1 && !checkForWin()) {
            counter.setImageResource(R.drawable.ic_cross);
            mCellIndex.set(tag, isX);
            isX = 2;
            view.setClickable(false);
        } else if (isX == 2 && !checkForWin()) {
            counter.setImageResource(R.drawable.ic_zero);
            mCellIndex.set(tag, isX);
            isX = 1;
            view.setClickable(false);
        }

        fullBoard();
        checkForWin();
    }

    public void resetBoard() {

        Log.i(LOG_TAG, "new game button was clicked");

        mCellIndex = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
        hideWinningLines();
        checkForWin();

        for (int i = 0; i < activityBinding.gridLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) activityBinding.gridLayout.getChildAt(i);
            imageView.setImageResource(0);
            imageView.setClickable(true);
        }
    }

    public boolean fullBoard() {
        // iterate in the whole list and read the mCellStatus of the mCellIndex

        for (int i = 0; i < mCellIndex.size(); i++) {
            if (mCellIndex.get(i) == 0) {
                return false;
            }
        }
        showToast("It's a draw");
        return true;
    }


    public boolean checkForWin() {
        if (checkRowsX() || checkRowsZero() || checkColumnsX() || checkColumnsZero() || checkDiagonalsX() || checkDiagonalsZero()) {
            announceWinner();
            setClickableFalse();
            return true;
        } else {
            return false;
        }
    }

    public void announceWinner() {
        if (isXWinner) {
            showToast("Player 1 has won! (X)");
            viewModelX();
        } else {
            showToast("Player 2 has won! (O)");
            viewModelO();
        }
    }

    public void viewModelX() {
        readFromViewModelX();
        counterPlayer1++;
        writeToViewModelX();
        displayCounterX();
    }

    public void viewModelO() {
        readFromViewModelX();
        counterPlayer2++;
        writeToViewModelO();
        displayCounterO();
    }


    public boolean checkRowsX() {
        if (mCellIndex.get(0) == 1 && mCellIndex.get(1) == 1 && mCellIndex.get(2) == 1) {
            isXWinner = true;
            activityBinding.topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(3) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(5) == 1) {
            isXWinner = true;
            activityBinding.centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(6) == 1 && mCellIndex.get(7) == 1 && mCellIndex.get(8) == 1) {
            isXWinner = true;
            activityBinding.bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = false;
            return false;
        }
    }


    public boolean checkRowsZero() {
        if (mCellIndex.get(0) == 2 && mCellIndex.get(1) == 2 && mCellIndex.get(2) == 2) {
            isXWinner = false;
            activityBinding.topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(3) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(5) == 2) {
            isXWinner = false;
            activityBinding.centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(6) == 2 && mCellIndex.get(7) == 2 && mCellIndex.get(8) == 2) {
            isXWinner = false;
            activityBinding.bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = true;
            return false;
        }
    }

    public boolean checkColumnsX() {
        if (mCellIndex.get(0) == 1 && mCellIndex.get(3) == 1 && mCellIndex.get(6) == 1) {
            isXWinner = true;
            activityBinding.leftVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(1) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(7) == 1) {
            isXWinner = true;
            activityBinding.centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 1 && mCellIndex.get(5) == 1 && mCellIndex.get(8) == 1) {
            isXWinner = true;
            activityBinding.rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkColumnsZero() {
        if (mCellIndex.get(0) == 2 && mCellIndex.get(3) == 2 && mCellIndex.get(6) == 2) {
            isXWinner = false;
            activityBinding.leftVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(1) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(7) == 2) {
            isXWinner = false;
            activityBinding.centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 2 && mCellIndex.get(5) == 2 && mCellIndex.get(8) == 2) {
            isXWinner = false;
            activityBinding.rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = true;
            return false;
        }
    }


    public boolean checkDiagonalsX() {
        if (mCellIndex.get(0) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(8) == 1) {
            isXWinner = true;
            activityBinding.leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(6) == 1) {
            isXWinner = true;
            activityBinding.rightLeftDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = false;
            return false;
        }
    }

    public boolean checkDiagonalsZero() {
        if (mCellIndex.get(0) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(8) == 2) {
            isXWinner = false;
            activityBinding.leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(6) == 2) {
            activityBinding.rightLeftDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }


    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_round) {

            resetBoard();
        } else if (item.getItemId() == R.id.action_new_game) {

            resetBoard();
            initializePlayers();
        } else if (item.getItemId() == R.id.action_log_out) {

            showToast("Log out");

            String userString = getFirebaseUser().getEmail().substring(0, getFirebaseUser().getEmail().indexOf("@"));
            FirebaseAuth.getInstance().signOut();
            String name = userString + " X:";
            activityBinding.player1Text.setText(name);

        } else if (item.getItemId() == R.id.action_settings) {

            Intent settingsIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideWinningLines() {
        activityBinding.topHorizontal.setVisibility(View.INVISIBLE);
        activityBinding.centerHorizontal.setVisibility(View.INVISIBLE);
        activityBinding.bottomHorizontal.setVisibility(View.INVISIBLE);
        activityBinding.leftVertical.setVisibility(View.INVISIBLE);
        activityBinding.centerVertical.setVisibility(View.INVISIBLE);
        activityBinding.rightVertical.setVisibility(View.INVISIBLE);
        activityBinding.leftRightDiagonal.setVisibility(View.INVISIBLE);
        activityBinding.rightLeftDiagonal.setVisibility(View.INVISIBLE);
    }


    public void initializePlayers() {
        activityBinding.player1Result.setText("0");
        activityBinding.player2Result.setText("0");

        counterPlayer1 = 0;
        counterPlayer2 = 0;
    }


    public void updateCounters() {
        readFromViewModelX();
        readFromViewModelO();

        displayCounterX();
        displayCounterO();
    }

    public void readFromViewModelX() {
        counterPlayer1 = mScoreViewModel.getScorePlayerX();
    }

    public void writeToViewModelX() {
        mScoreViewModel.setScorePlayerX(counterPlayer1);
    }

    public void readFromViewModelO() {
        counterPlayer2 = mScoreViewModel.getScorePlayerO();
    }

    public void writeToViewModelO() {
        mScoreViewModel.setScorePlayerO(counterPlayer2);
    }

    public void displayCounterX() {
        activityBinding.player1Result.setText(String.valueOf(counterPlayer1));
    }

    public void displayCounterO() {
        activityBinding.player2Result.setText(String.valueOf(counterPlayer2));
    }

    public void setClickableFalse() {
        for (int i = 0; i < activityBinding.gridLayout.getChildCount(); i++) {
            activityBinding.gridLayout.getChildAt(i).setClickable(false);
        }
    }
}
