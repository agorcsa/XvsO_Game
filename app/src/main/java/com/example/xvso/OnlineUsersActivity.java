package com.example.xvso;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.xvso.databinding.ActivityOnlineUsersBinding;
import com.example.xvso.firebase.BaseActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class OnlineUsersActivity extends BaseActivity {

    private static final String LOG_TAG = "OnlineUsersActivity";

    private ActivityOnlineUsersBinding usersBinding;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<String> loggedUsersArrayList = new ArrayList();
    private ArrayAdapter loggedUsersArrayAdapter;

    private ListView requestedUsersListView;
    private ArrayList<String> requestedUsersArrayList = new ArrayList<>();
    private ArrayAdapter requestedUsersArrayAdapter;

    private TextView userIdTextView;
    private String LoginUID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);

        usersBinding = DataBindingUtil.setContentView(this, R.layout.activity_online_users);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();


        loggedUsersArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        usersBinding.loggedUsersListview.setAdapter(loggedUsersArrayAdapter);

        usersBinding.sendRequestTextview.setText(getString(R.string.please_wait));
        usersBinding.sendRequestTextview.setText(getString(R.string.please_wait));

        requestedUsersArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        usersBinding.requestedUsersListview.setAdapter(requestedUsersArrayAdapter);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    LoginUID = user.getUid();
                        Log.d(LOG_TAG, "onAuthStateChanged:signed_in: " + LoginUID);


                }
            }
        };

    }
}
