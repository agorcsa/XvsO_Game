package com.example.xvso;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.xvso.Objects.GameItem;
import com.example.xvso.databinding.ActivityNewGameBinding;

import java.util.ArrayList;

public class NewGameActivity extends AppCompatActivity {

    private ActivityNewGameBinding newGameBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newGameBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_game);

        ArrayList<GameItem> openGames = new ArrayList<>();

        // add the games from Firebase
        // placeholder code
        openGames.add(new GameItem(R.drawable.ic_zero, "Game 1" , "user 1"));
        openGames.add(new GameItem(R.drawable.ic_zero, "Game 2" , "user 2"));
        openGames.add(new GameItem(R.drawable.ic_zero, "Game 3" , "user 3"));
    }
}
