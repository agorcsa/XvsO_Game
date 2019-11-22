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


public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = "MainActivity";

    private ScoreViewModel mScoreViewModel;

    private ActivityMainBinding activityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mScoreViewModel = ViewModelProviders.of(this).get(ScoreViewModel.class);
        activityBinding.setViewModel(mScoreViewModel);
    }

    // UI related
    // used at device rotation
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
            preserveBoard();
        }
    }

    public void dropIn(View view) {
        // Animate view
        ImageView counter = (ImageView) view;
        counter.setTranslationY(-1000f);
        counter.animate().translationYBy(1000f).setDuration(300);

        // Play move
        mScoreViewModel.setTag(Integer.parseInt((String) counter.getTag()));
        if (mScoreViewModel.getIsX() == 1 && !mScoreViewModel.checkForWin()) {
            counter.setImageResource(R.drawable.ic_cross);
            mScoreViewModel.getCellIndex().set(mScoreViewModel.getTag(), mScoreViewModel.getIsX());
            Log.i(LOG_TAG, "mCellIndex: " + mScoreViewModel.getCellIndex());
            mScoreViewModel.setIsX(2);
            view.setClickable(false);
        } else if (mScoreViewModel.getIsX() == 2 && !mScoreViewModel.checkForWin()) {
            counter.setImageResource(R.drawable.ic_zero);
            mScoreViewModel.getCellIndex().set(mScoreViewModel.getTag(), mScoreViewModel.getIsX());
            mScoreViewModel.setIsX(1);
            view.setClickable(false);
        }

        if (mScoreViewModel.checkForWin()) {
            // Game finished
            // resetBoardUI();
            // Announce winner
            announceWinner();
        } else if (mScoreViewModel.fullBoard()) {
            showToast("It's a draw");
        } else {
            // Toggle player
            mScoreViewModel.setIsX(2);
        }
    }

    // UI related
    // empties the board
    public void resetBoardUI() {

        if (mScoreViewModel.resetBoard()) {

            hideWinningLines();
            mScoreViewModel.checkForWin();

            for (int i = 0; i < activityBinding.gridLayout.getChildCount(); i++) {
                ImageView imageView = (ImageView) activityBinding.gridLayout.getChildAt(i);
                imageView.setImageResource(0);
                imageView.setClickable(true);
            }
        }
    }

    // UI related
    // displays a toast on the screen in case of draw
    public boolean fullBoardUI() {
        if (mScoreViewModel.fullBoard()) {
            showToast("It's a draw");
        }
        return true;
    }

    // used in case of device rotation
    public void preserveBoard() {
        for (int i = 0; i < mScoreViewModel.getCellIndex().size(); i++) {
            ImageView cell = (ImageView) activityBinding.gridLayout.getChildAt(i);
            if (mScoreViewModel.getCellIndex().get(i) == 1) {
                cell.setImageResource(R.drawable.ic_cross);
                cell.setClickable(false);
            } else if ((mScoreViewModel.getCellIndex().get(i) == 2)) {
                cell.setImageResource(R.drawable.ic_zero);
                cell.setClickable(false);
            }
        }
    }

    // UI related
    // displays the winner on the screen
    public void announceWinner() {
        if (mScoreViewModel.isWinner()) {
            showToast("Player 1 has won! (X)");
            mScoreViewModel.viewModelX();
            activityBinding.player1Result.setText(String.valueOf(mScoreViewModel.getCounterPlayer1()));
        } else {
            showToast("Player 2 has won! (O)");
            mScoreViewModel.viewModelO();
            activityBinding.player2Result.setText(String.valueOf(mScoreViewModel.getCounterPlayer2()));
        }
    }

    // UI related
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // UI related
    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // UI related
    // menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_round) {

            resetBoardUI();
        } else if (item.getItemId() == R.id.action_new_game) {

            resetBoardUI();
            initializePlayersUI();

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

    // UI related
    // hides each winning line
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

    // UO related
    // initializes the players' result
    public void initializePlayersUI() {

        if (mScoreViewModel.initializePlayers()) {

            activityBinding.player1Result.setText("0");
            activityBinding.player2Result.setText("0");
        }
    }

    // UI related
    // updates the counters
    public void updateCounters() {
        mScoreViewModel.readFromViewModelX();
        mScoreViewModel.readFromViewModelO();

        activityBinding.player1Result.setText(String.valueOf(mScoreViewModel.getCounterPlayer1()));
        activityBinding.player2Result.setText(String.valueOf(mScoreViewModel.getCounterPlayer2()));
    }

    // UI related
    // could be used to make the grid cells not clickable
    public void setClickableFalse() {
        for (int i = 0; i < activityBinding.gridLayout.getChildCount(); i++) {
            activityBinding.gridLayout.getChildAt(i).setClickable(false);
        }
    }
}
