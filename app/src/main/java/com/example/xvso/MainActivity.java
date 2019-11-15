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
import com.example.xvso.firebase.LoginActivity;
import com.example.xvso.firebase.ProfileActivity;
import com.example.xvso.viewmodel.ScoreViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = "MainActivity";

    private ScoreViewModel mScoreViewModel;

    private ActivityMainBinding activityBinding;

    private ImageView counterVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mScoreViewModel = ViewModelProviders.of(this).get(ScoreViewModel.class);
    }

    protected void onResume() {
        super.onResume();

        if (getFirebaseUser() != null) {
            mScoreViewModel.setDisplayName(getFirebaseUser().getDisplayName());

            if (mScoreViewModel.getDisplayName() != null && !mScoreViewModel.getDisplayName().isEmpty()) {
                activityBinding.player1Text.setText(mScoreViewModel.getDisplayName() + " X:");
            } else {
                String userString = getFirebaseUser().getEmail().substring(0, getFirebaseUser().getEmail().indexOf("@"));
                activityBinding.player1Text.setText(userString + " X:");
            }
            updateCounters();
        }
    }

    public void dropIn(View view) {

        ImageView counter = (ImageView) view;

        counter.setTranslationY(-1000f);

        counter.animate().translationYBy(1000f).setDuration(300);

        mScoreViewModel.setTag(Integer.parseInt((String) counter.getTag()));
        //Toast.makeText(this, "Tag: " + mScoreViewModel.getTag(), Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Clicked tag: " + mScoreViewModel.getTag());

        if (mScoreViewModel.getIsX() == 1 && !checkForWin()) {
            counter.setImageResource(R.drawable.ic_cross);
            mScoreViewModel.getCellIndex().set(mScoreViewModel.getTag(), mScoreViewModel.getIsX());
            Log.i(LOG_TAG, "mCellIndex: " + mScoreViewModel.getCellIndex());
            mScoreViewModel.setIsX(2);
            view.setClickable(false);
        } else if (mScoreViewModel.getIsX() == 2 && !checkForWin()) {
            counter.setImageResource(R.drawable.ic_zero);
            mScoreViewModel.getCellIndex().set(mScoreViewModel.getTag(), mScoreViewModel.getIsX());
            mScoreViewModel.setIsX(1);
            view.setClickable(false);
        }

        fullBoard();
        checkForWin();
    }

    public void resetBoard() {

        Log.i(LOG_TAG, "new game button was clicked");

        mScoreViewModel.setCellIndex(new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0)));

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

        for (int i = 0; i < mScoreViewModel.getCellIndex().size(); i++) {
            if (mScoreViewModel.getCellIndex().get(i) == 0) {
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
        if (mScoreViewModel.XWinner()) {
            showToast("Player 1 has won! (X)");
            viewModelX();
        } else {
            showToast("Player 2 has won! (O)");
            viewModelO();
        }
    }

    public void viewModelX() {
        readFromViewModelX();
        mScoreViewModel.setCounterPlayer1(mScoreViewModel.getCounterPlayer1() + 1);
        writeToViewModelX();
        displayCounterX();
    }

    public void viewModelO() {
        readFromViewModelX();
        mScoreViewModel.setCounterPlayer2(mScoreViewModel.getCounterPlayer2() + 1);
        writeToViewModelO();
        displayCounterO();
    }


    public boolean checkRowsX() {
        if (mScoreViewModel.getCellIndex().get(0) == 1 && mScoreViewModel.getCellIndex().get(1) == 1 && mScoreViewModel.getCellIndex().get(2) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(3) == 1 && mScoreViewModel.getCellIndex().get(4) == 1 && mScoreViewModel.getCellIndex().get(5) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(6) == 1 && mScoreViewModel.getCellIndex().get(7) == 1 && mScoreViewModel.getCellIndex().get(8) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            mScoreViewModel.setXWinner(false);;
            return false;
        }
    }


    public boolean checkRowsZero() {
        if (mScoreViewModel.getCellIndex().get(0) == 2 && mScoreViewModel.getCellIndex().get(1) == 2 && mScoreViewModel.getCellIndex().get(2) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(3) == 2 && mScoreViewModel.getCellIndex().get(4) == 2 && mScoreViewModel.getCellIndex().get(5) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(6) == 2 && mScoreViewModel.getCellIndex().get(7) == 2 && mScoreViewModel.getCellIndex().get(8) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            mScoreViewModel.setXWinner(true);
            return false;
        }
    }

    public boolean checkColumnsX() {
        if (mScoreViewModel.getCellIndex().get(0) == 1 && mScoreViewModel.getCellIndex().get(3) == 1 && mScoreViewModel.getCellIndex().get(6) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.leftVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(1) == 1 && mScoreViewModel.getCellIndex().get(4) == 1 && mScoreViewModel.getCellIndex().get(7) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(2) == 1 && mScoreViewModel.getCellIndex().get(5) == 1 && mScoreViewModel.getCellIndex().get(8) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkColumnsZero() {
        if (mScoreViewModel.getCellIndex().get(0) == 2 && mScoreViewModel.getCellIndex().get(3) == 2 && mScoreViewModel.getCellIndex().get(6) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.leftVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(1) == 2 && mScoreViewModel.getCellIndex().get(4) == 2 && mScoreViewModel.getCellIndex().get(7) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(2) == 2 && mScoreViewModel.getCellIndex().get(5) == 2 && mScoreViewModel.getCellIndex().get(8) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            mScoreViewModel.setXWinner(true);
            return false;
        }
    }


    public boolean checkDiagonalsX() {
        if (mScoreViewModel.getCellIndex().get(0) == 1 && mScoreViewModel.getCellIndex().get(4) == 1 && mScoreViewModel.getCellIndex().get(8) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(2) == 1 && mScoreViewModel.getCellIndex().get(4) == 1 && mScoreViewModel.getCellIndex().get(6) == 1) {
            mScoreViewModel.setXWinner(true);
            activityBinding.rightLeftDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else {
            mScoreViewModel.setXWinner(false);
            return false;
        }
    }

    public boolean checkDiagonalsZero() {
        if (mScoreViewModel.getCellIndex().get(0) == 2 && mScoreViewModel.getCellIndex().get(4) == 2 && mScoreViewModel.getCellIndex().get(8) == 2) {
            mScoreViewModel.setXWinner(false);
            activityBinding.leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mScoreViewModel.getCellIndex().get(2) == 2 && mScoreViewModel.getCellIndex().get(4) == 2 && mScoreViewModel.getCellIndex().get(6) == 2) {
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

        } else if (item.getItemId() == R.id.action_watch_video) {

           Intent intent = new Intent(MainActivity.this, VideoActivity.class);
           startActivity(intent);

        } else if (item.getItemId() == R.id.action_log_out) {

            showToast("Log out");

            FirebaseAuth.getInstance().signOut();

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);

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

        mScoreViewModel.setCounterPlayer1(0);
        mScoreViewModel.setCounterPlayer2(0);
    }

    public void updateCounters() {
        readFromViewModelX();
        readFromViewModelO();

        displayCounterX();
        displayCounterO();
    }

    public void readFromViewModelX() {
        mScoreViewModel.setCounterPlayer1(mScoreViewModel.getScorePlayerX());
    }

    public void writeToViewModelX() {
        mScoreViewModel.setScorePlayerX(mScoreViewModel.getCounterPlayer1());
    }

    public void readFromViewModelO() {
        mScoreViewModel.setCounterPlayer2(mScoreViewModel.getScorePlayerO());
    }

    public void writeToViewModelO() {
        mScoreViewModel.setScorePlayerO(mScoreViewModel.getCounterPlayer2());
    }

    public void displayCounterX() {
        activityBinding.player1Result.setText(String.valueOf(mScoreViewModel.getCounterPlayer1()));
    }

    public void displayCounterO() {
        activityBinding.player2Result.setText(String.valueOf(mScoreViewModel.getCounterPlayer2()));
    }

    public void setClickableFalse() {
        for (int i = 0; i < activityBinding.gridLayout.getChildCount(); i++) {
            activityBinding.gridLayout.getChildAt(i).setClickable(false);
        }
    }
}
