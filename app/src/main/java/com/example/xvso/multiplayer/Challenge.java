package com.example.xvso.multiplayer;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Challenge {

    public String opener;
    public String gameRef;

    public Challenge() {
        // required by Firebase database
    }

    public Challenge(String opener, String gameRef) {
        this.gameRef = gameRef;
        this.opener = opener;
    }
}
