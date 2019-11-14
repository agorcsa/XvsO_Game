package com.example.xvso.firebaseutils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseLiveData extends LiveData<DataSnapshot> {

    private static final String LOG_TAG = "FirebaseLiveData";

    private final Query query;

    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseLiveData(Query query) {
        this.query = query;
    }

    public FirebaseLiveData(DatabaseReference databaseReference) {
        this.query = databaseReference;
    }

    @Override
    protected void onActive() {
        super.onActive();
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
