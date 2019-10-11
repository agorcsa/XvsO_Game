package com.example.bitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MainActivity";

    // key constants used to save the value of the players' score in onSaveInstanceState()
    public static final String SCORE_PLAYER_1 = "player_1_score";
    public static final String SCORE_PLAYER_2 = "player_2_score";

    // represents the tag of each cell of the grid
    // (0, 1, 2)
    // (3, 4, 5)
    // (6, 7, 8)
    public int tag;

    // if variable isX = 2, the cell stores a "O" not an "X"
    public int isX = 1;

    public boolean isXWinner;

    ArrayList<Integer> mCellIndex = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));

    private GridLayout gridLayout;

    // represent the cells of the grid layout
    private AppCompatImageView block1;
    private AppCompatImageView block2;
    private AppCompatImageView block3;
    private AppCompatImageView block4;
    private AppCompatImageView block5;
    private AppCompatImageView block6;
    private AppCompatImageView block7;
    private AppCompatImageView block8;
    private AppCompatImageView block9;

    // represent the wining lines of the game
    private View lefVertical;
    private View centerVertical;
    private View rightVertical;
    private View topHorizontal;
    private View centerHorizontal;
    private View bottomHorizontal;
    private View rightLeftDiagonal;
    private View leftRightDiagonal;

    // displays the value of counterPlayer1 and counterPlayer2;
    private TextView player1Result;
    private TextView player2Result;

    // keeps track of the score of both players
    private int counterPlayer1 = 0;
    private int counterPlayer2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.grid_layout);

        // cells of the grid layout
        block1 = findViewById(R.id.block_1);
        block2 = findViewById(R.id.block_2);
        block3 = findViewById(R.id.block_3);
        block4 = findViewById(R.id.block_4);
        block5 = findViewById(R.id.block_5);
        block6 = findViewById(R.id.block_6);
        block7 = findViewById(R.id.block_7);
        block8 = findViewById(R.id.block_8);
        block9 = findViewById(R.id.block_9);

        // winning lines
        lefVertical = findViewById(R.id.left_vertical);
        centerVertical = findViewById(R.id.center_vertical);
        rightVertical = findViewById(R.id.right_vertical);
        topHorizontal = findViewById(R.id.top_horizontal);
        centerHorizontal = findViewById(R.id.center_horizontal);
        bottomHorizontal = findViewById(R.id.bottom_horizontal);
        rightLeftDiagonal = findViewById(R.id.right_left_diagonal);
        leftRightDiagonal = findViewById(R.id.left_right_diagonal);
        player1Result = findViewById(R.id.player1_result);
        player2Result = findViewById(R.id.player2_result);

        initializePlayers();
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

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) gridLayout.getChildAt(i);
            imageView.setImageResource(0);

            mCellIndex = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));

            imageView.setClickable(true);

            hideWinningLines();
            ;
            checkForWin();
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
            counterPlayer1++;
            player1Result.setText(String.valueOf(counterPlayer1));
        } else {
            showToast("Player 2 has won! (O)");
            counterPlayer2++;
            player2Result.setText(String.valueOf(counterPlayer2));
        }
    }

    public boolean checkRowsX() {
        if (mCellIndex.get(0) == 1 && mCellIndex.get(1) == 1 && mCellIndex.get(2) == 1) {
            isXWinner = true;
            topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(3) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(5) == 1) {
            isXWinner = true;
            centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(6) == 1 && mCellIndex.get(7) == 1 && mCellIndex.get(8) == 1) {
            isXWinner = true;
            bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = false;
            return false;
        }
    }


    public boolean checkRowsZero() {
        if (mCellIndex.get(0) == 2 && mCellIndex.get(1) == 2 && mCellIndex.get(2) == 2) {
            isXWinner = false;
            topHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(3) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(5) == 2) {
            isXWinner = false;
            centerHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(6) == 2 && mCellIndex.get(7) == 2 && mCellIndex.get(8) == 2) {
            isXWinner = false;
            bottomHorizontal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = true;
            return false;
        }
    }

    public boolean checkColumnsX() {
        if (mCellIndex.get(0) == 1 && mCellIndex.get(3) == 1 && mCellIndex.get(6) == 1) {
            isXWinner = true;
            lefVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(1) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(7) == 1) {
            isXWinner = true;
            centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 1 && mCellIndex.get(5) == 1 && mCellIndex.get(8) == 1) {
            isXWinner = true;
            rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkColumnsZero() {
        if (mCellIndex.get(0) == 2 && mCellIndex.get(3) == 2 && mCellIndex.get(6) == 2) {
            isXWinner = false;
            lefVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(1) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(7) == 2) {
            isXWinner = false;
            centerVertical.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 2 && mCellIndex.get(5) == 2 && mCellIndex.get(8) == 2) {
            isXWinner = false;
            rightVertical.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = true;
            return false;
        }
    }


    public boolean checkDiagonalsX() {
        if (mCellIndex.get(0) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(8) == 1) {
            isXWinner = true;
            leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 1 && mCellIndex.get(4) == 1 && mCellIndex.get(6) == 1) {
            isXWinner = true;
            rightLeftDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else {
            isXWinner = false;
            return false;
        }
    }

    public boolean checkDiagonalsZero() {
        if (mCellIndex.get(0) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(8) == 2) {
            isXWinner = false;
            leftRightDiagonal.setVisibility(View.VISIBLE);
            return true;
        } else if (mCellIndex.get(2) == 2 && mCellIndex.get(4) == 2 && mCellIndex.get(6) == 2) {
            rightLeftDiagonal.setVisibility(View.VISIBLE);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideWinningLines() {

        topHorizontal.setVisibility(View.INVISIBLE);
        centerHorizontal.setVisibility(View.INVISIBLE);
        bottomHorizontal.setVisibility(View.INVISIBLE);

        lefVertical.setVisibility(View.INVISIBLE);
        centerVertical.setVisibility(View.INVISIBLE);
        rightVertical.setVisibility(View.INVISIBLE);

        leftRightDiagonal.setVisibility(View.INVISIBLE);
        rightLeftDiagonal.setVisibility(View.INVISIBLE);
    }


    public void initializePlayers() {
        player1Result.setText("0");
        player2Result.setText("0");
        counterPlayer1 = 0;
        counterPlayer2 = 0;
    }

    public void setClickableFalse(){
        block1.setClickable(false);
        block2.setClickable(false);
        block3.setClickable(false);
        block4.setClickable(false);
        block5.setClickable(false);
        block6.setClickable(false);
        block7.setClickable(false);
        block8.setClickable(false);
        block9.setClickable(false);
    }
}
