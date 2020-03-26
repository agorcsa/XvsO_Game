package com.example.xvso;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xvso.Objects.Game;
import com.example.xvso.adapter.GameAdapter;

import java.util.ArrayList;

public class NewGameActivity extends AppCompatActivity {

    private ArrayList<Game> mOpenGamesList;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private Button newGameButton;
    private EditText insertGameEditText;
    private EditText removeGameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        createGameList();
        buildRecyclerView();

        newGameButton = findViewById(R.id.new_game);
        insertGameEditText = findViewById(R.id.insert_game_edit_text);
        removeGameEditText = findViewById(R.id.remove_game_edit_text);
    }


    public void createGameList() {
        // add the games from Firebase
        // placeholder/dummy code
        mOpenGamesList = new ArrayList<>();

        mOpenGamesList.add(new Game(R.drawable.profile, "Game 1" , "user 1", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 2" , "user 2", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 3" , "user 3", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 4" , "user 4", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 5" , "user 5", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 6" , "user 6", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 7" , "user 7", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 8" , "user 8", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 9" , "user 9", 0));
        mOpenGamesList.add(new Game(R.drawable.profile, "Game 10" , "user 10", 0));
    }

    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.games_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new GameAdapter(mOpenGamesList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void onNewGameButtonClicked(View view) {
        Toast.makeText(getApplicationContext(), "A new game has been created", Toast.LENGTH_LONG).show();
        int position = Integer.parseInt(insertGameEditText.getText().toString());
        addNewGame(position);
    }

    public void addNewGame(int position) {
        mOpenGamesList.add(position, new Game(R.drawable.ic_cross, "A new game has been added at position" + position, "Opponent User Name", 0));
        adapter.notifyDataSetChanged();
    }

    public void removeGameFromList(View view) {
        Toast.makeText(getApplicationContext(), "The game has been removed", Toast.LENGTH_LONG).show();
        int position = Integer.parseInt(insertGameEditText.getText().toString());
        removeGame(position);
    }

    public void removeGame(int position) {
        mOpenGamesList.remove(position);
        adapter.notifyDataSetChanged();
    }
}


