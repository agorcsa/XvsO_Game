package com.example.xvso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.xvso.databinding.ActivityOnlineUsersBinding;
import com.example.xvso.firebase.BaseActivity;
import com.example.xvso.viewmodel.OnlineUsersViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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
    private String LoginUserID;
    private String UserName;

    private OnlineUsersViewModel onlineUsersViewModel;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);

        usersBinding = DataBindingUtil.setContentView(this, R.layout.activity_online_users);
        onlineUsersViewModel = ViewModelProviders.of(this).get(OnlineUsersViewModel.class);
        usersBinding.setViewModel(onlineUsersViewModel);
        usersBinding.setLifecycleOwner(this);

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
                        LoginUID = user.getEmail();
                        usersBinding.userLoginTextview.setText(LoginUserID);
                        UserName = convertEmailToString(LoginUserID);

                        myRef.child("users").child(UserName).child("request").setValue(LoginUID);
                        requestedUsersArrayAdapter.clear();
                        acceptIncomingRequests();
                } else {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out or login");
                        joinOnlineGame();
                }
            }
        };

    }

    private String convertEmailToString(String email) {

        UserName = email.substring(0, getFirebaseUser().getEmail().indexOf("@"));

        return UserName;
    }

    private void acceptIncomingRequests() {
        myRef.child("users").child(UserName).child("request")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {

                            HashMap<String,Object> map = (HashMap<String, Object>)dataSnapshot.getValue();
                            if (map != null) {
                                String value = "";

                                for (String key:map.keySet()) {
                                    value = (String) map.get(key);
                                    requestedUsersArrayAdapter.add(convertEmailToString(value));
                                    requestedUsersArrayAdapter.notifyDataSetChanged();
                                    myRef.child("users").child(UserName).child("request").setValue(LoginUID);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void joinOnlineGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.login_dialog, null);
        builder.setView(dialogView);

        final EditText emailEditText = dialogView.findViewById(R.id.login_email);
        final EditText passwordEditText = dialogView.findViewById(R.id.login_password);

        builder.setTitle("Please register");
        builder.setMessage("Enter your email and password for registration");
        builder.setPositiveButton(" Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // register user

            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser(String email, String password) {

    }
}
