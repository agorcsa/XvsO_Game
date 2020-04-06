package com.example.xvso;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;

import com.example.xvso.Objects.Game;
import com.google.firebase.database.DataSnapshot;

@RequiresApi(api = Build.VERSION_CODES.N)
public class GameDeserializer implements Function<DataSnapshot, Game> {
    @Override
    public Game apply(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Game.class);
    }
}
