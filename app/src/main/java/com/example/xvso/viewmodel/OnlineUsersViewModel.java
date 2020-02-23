package com.example.xvso.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.xvso.Deserializer;
import com.example.xvso.User;
import com.example.xvso.firebaseutils.FirebaseQueryLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OnlineUsersViewModel extends ViewModel {

    private static final String LOG_TAG = "OnlineUsersViewModel";

    private FirebaseAuth auth;
    private DatabaseReference query;

    private LiveData<User> userLiveData;
    private MutableLiveData<AlertDialogStatus> status = new MutableLiveData<>();

    public OnlineUsersViewModel() {

        auth = FirebaseAuth.getInstance();

        if (auth.getUid() != null) {

            query = FirebaseDatabase.getInstance().getReference("users").child((auth.getUid()));

            FirebaseQueryLiveData resultLiveData = new FirebaseQueryLiveData(query);
            userLiveData = Transformations.map(resultLiveData, new Deserializer());
        }
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void setUserLiveData(LiveData<User> userLiveData) {
        this.userLiveData = userLiveData;
    }

    public MutableLiveData<AlertDialogStatus> getStatus() {
        return status;
    }

    public void setStatus(MutableLiveData<AlertDialogStatus> status) {
        this.status = status;
    }

    public enum AlertDialogStatus {
        CONNECTING,
        CONNECTED
    }
}
