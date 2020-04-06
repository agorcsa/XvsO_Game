package com.example.xvso.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.xvso.GameDeserializer;
import com.example.xvso.Objects.Game;
import com.example.xvso.firebaseutils.FirebaseQueryLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OnlineGameViewModel extends BaseViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String MULTIPLAYER = "multiplayer";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference query;

    private MutableLiveData<ArrayList<Integer>> boardLiveData = new MutableLiveData<>();
    private LiveData<Game> gameLiveData;

    private String gameID = "";

    public OnlineGameViewModel(String gameID) {
        this.gameID = gameID;

          if (gameID != null) {

            query = FirebaseDatabase.getInstance().getReference(MULTIPLAYER).child((gameID));

            FirebaseQueryLiveData resultLiveData = new FirebaseQueryLiveData(query);
            gameLiveData = Transformations.map(resultLiveData, new GameDeserializer());
        }
    }
}
