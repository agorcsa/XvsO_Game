package com.example.xvso;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mScoreViewModel = ViewModelProviders.of(this).get(ScoreViewModel.class);
        activityBinding.setViewModel(mScoreViewModel);
        activityBinding.setLifecycleOwner(this);
    }

    public void dropIn(View view) {

        // animate
        ImageView counter = (ImageView) view;
        counter.setTranslationY(-1000f);
        counter.animate().translationYBy(1000f).setDuration(300);

        // play
        mScoreViewModel.play(Integer.parseInt((String) view.getTag()));

        if (mScoreViewModel.checkForWin()) {

            mScoreViewModel.gameEnded();
            announceWinner();
        } else if (mScoreViewModel.fullBoard()) {

            showToast("It's a draw");
        } else {
            mScoreViewModel.togglePlayer();
        }
    }

    // UI related
    // displays the winner on the screen
    public void announceWinner() {
        int team = mScoreViewModel.getCurrentTeam().getTeamType();

        if (mScoreViewModel.checkForWin()) {
            if (team == Team.TEAM_X) {
                showToast("Player 1 has won! (X)");
                mScoreViewModel.updateScore();
                activityBinding.player1Result.setText(String.valueOf(mScoreViewModel.getCurrentTeam().getTeamScore()));
            } else {
                showToast("Player 2 has won! (O)");
                mScoreViewModel.updateScore();
                activityBinding.player2Result.setText(String.valueOf(mScoreViewModel.getCurrentTeam().getTeamScore()));
            }
        }
    }

    // UI related
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
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

            mScoreViewModel.resetGame();
            hideChips();

        } else if (item.getItemId() == R.id.action_new_game) {

            mScoreViewModel.resetGame();
            hideChips();
            // no need to reset the score, as boardLiveData.setValue is being called on an empty board

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

    public void hideChips() {
        for (int i = 0; i < activityBinding.gridLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) activityBinding.gridLayout.getChildAt(i);
            imageView.setImageResource(0);
            imageView.setClickable(true);
        }
    }
}
