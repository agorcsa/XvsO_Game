package com.example.xvso.firebaseutils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserViewModel extends ViewModel {

    private static final DatabaseReference USERS_REF = FirebaseDatabase.getInstance().getReference("users");

    private final FirebaseLiveData liveData = new FirebaseLiveData(USERS_REF);

    // getter method for liveData
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
