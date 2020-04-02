package com.example.xvso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.xvso.Objects.Game;
import com.example.xvso.Objects.User;
import com.example.xvso.adapter.GameAdapter;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class OnlineUsersActivity extends BaseActivity implements GameAdapter.JoinGameClick {

    private static final String LOG_TAG = "OnlineUsersActivity";
    private static final String MULTIPLAYER = "multiplayer";
    private static final String PLAYER_SESSION = "player_session";
    private static final String GUEST = "guest";

    private static final int REQUEST_NOT_ACCEPTED = 0;
    private static final int REQUEST_ACCEPTED = 1;

    private static final String STATUS = "status";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private ActivityOnlineUsersBinding usersBinding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    // triggered at user sign-in, sign-out, or change, or when the listener was registered
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<String> loggedUsersArrayList = new ArrayList();
    private ArrayAdapter loggedUsersArrayAdapter;
    //    private ArrayAdapter requestedUsersArrayAdapter;
    private ListView requestedUsersListView;
    private ArrayList<String> requestedUsersArrayList = new ArrayList<>();
    private TextView userIdTextView;
    private String LoginUID;
    private String LoginUserID;
    private String userName;
    private Game game = new Game();
    private OnlineUsersViewModel onlineUsersViewModel;
    private ArrayList<Game> mOpenGamesList = new ArrayList<>();
    private ArrayList<Game> mOpenGames = new ArrayList<>();

    private LinearLayoutManager layoutManager;
    private GameAdapter gameAdapter;

    private User host;
    private User guest;

    private DatabaseReference query;

    private User currentUser = new User();
    private User myUser;

    private boolean newGame;
    private String key;

    private String guestFirstName;
    private String guestName;

    private TextView joinButton;

    private AlertDialog alertDialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usersBinding = DataBindingUtil.setContentView(this, R.layout.activity_online_users);
        onlineUsersViewModel = ViewModelProviders.of(this).get(OnlineUsersViewModel.class);

        joinButton = findViewById(R.id.join_game_text_view);

        usersBinding.setViewModel(onlineUsersViewModel);
        usersBinding.setLifecycleOwner(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        buildRecyclerView(currentUser);

        myRef.getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUser = dataSnapshot.getValue(User.class);

                updateLoginUsers(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        onlineUsersViewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                myUser = user;
                buildRecyclerView(myUser);
                readFromDatabase();
                LoginUserID = myUser.getEmailAddress();
                usersBinding.userLoginTextview.setText(LoginUserID);
            }
        });

        gameAdapter.notifyDataSetChanged();
    }


    public void startGame(String key) {

        Intent intent = new Intent(getApplicationContext(), OnlineGameActivity.class);

        String playerSession = database.getReference("multiplayer").child(key).getKey();

        intent.putExtra(PLAYER_SESSION, playerSession);

        startActivity(intent);
        finish();
    }


    public void updateLoginUsers(DataSnapshot dataSnapshot) {

        String key = "";
        Set<String> set = new HashSet<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);

            if (user != null) {
                String userEmail = user.getEmailAddress();

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {

                    String currentUserEmail = mAuth.getCurrentUser().getEmail();

                    if (userEmail != null && currentUserEmail != null) {
                        if (!userEmail.equals(currentUserEmail)) {
                            set.add(user.getName());
                        }
                    }
                }
            }
        }
    }


    private void acceptIncomingRequests() {
        myRef.child("users").child(userName).child("request")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {

                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (map != null) {
                                String value = "";

                                for (String key : map.keySet()) {
                                    value = (String) map.get(key);
//                                    requestedUsersArrayAdapter.add(convertEmailToString(value));
//                                    requestedUsersArrayAdapter.notifyDataSetChanged();
                                    myRef.child("users").child(LoginUID).child("request").setValue(LoginUID);
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

    public void buildRecyclerView(User user) {

        layoutManager = new LinearLayoutManager(this);
        gameAdapter = new GameAdapter(this, mOpenGamesList, user);
        usersBinding.gamesRecyclerView.setHasFixedSize(true);
        usersBinding.gamesRecyclerView.setLayoutManager(layoutManager);
        usersBinding.gamesRecyclerView.setAdapter(gameAdapter);
    }

    public void addNewGame() {

        if (myUser != null) {
            Game game = new Game();
            game.setHost(myUser);
            game.setGuest(guest);
            userName = game.getHost().getUserName();
            game.setUserName(userName);
            DatabaseReference newGameRef = myRef.child(MULTIPLAYER).push();
            key = newGameRef.getKey();
            game.setKey(key);
            newGameRef.setValue(game);

            if (key != null) {

                myRef.child(MULTIPLAYER).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //startGame();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }


    public void readFromDatabase() {

        Query query = database.getReference(MULTIPLAYER)
                .orderByChild(STATUS)
                .equalTo(Game.STATUS_WAITING);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mOpenGamesList.clear();

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Game game = item.getValue(Game.class);

                    mOpenGamesList.add(game);
                    User host = game.getHost();
                    String uidHost = host.getUID();
                    String UID = myUser.getUID();
                    // makes sure that the host can add only one game at a time
                    if (UID.equals(uidHost)) {
                        newGame = true;
                        key = item.getKey();
                        opponentJoinedGame(key);
                    }
                }

                if (!newGame) {
                    addNewGame();
                }

                if (newGame) {
                    opponentJoinedGame(key);
                }

                gameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public boolean userNameCheck(Boolean b) {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            LoginUserID = user.getEmail();

            b = userName.contains(".");

        }
        return b;
    }


    public boolean opponentJoinedGame(String key) {


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("multiplayer").child(key).child("guest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                guest = dataSnapshot.getValue(User.class);

                if (guest != null) {

                    String guestUID = guest.getUID();

                    if (!TextUtils.isEmpty(guestUID)) {
                        // Perhaps checking that the guest does not correspond to our currently logged in user?
                        if (!guest.getUID().equals(myUser.getUID())) {
                            showAlert(key);
                        }
                    } else {
                        game.setStatus(Game.STATUS_WAITING);
                        database.getReference("multiplayer").child(key).child("status").setValue(Game.STATUS_WAITING);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    @Override
    public void onJoinGameClick(String key) {

        database.getReference("multiplayer").child(key).child("guest").setValue(myUser);

        DatabaseReference ref = database.getReference(MULTIPLAYER).child(key).child("acceptedRequest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Integer isRequestAccepted = dataSnapshot.getValue(Integer.class);

                if (isRequestAccepted == REQUEST_ACCEPTED) {
                    startGame(key);
                    game.setStatus(Game.STATUS_PLAYING);
                    database.getReference("multiplayer").child(key).child("status").setValue(Game.STATUS_PLAYING);
                    //joinButton.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();

    }

    // shows AlertDialog
    // when guest sends a request to the host
    public void showAlert(String key) {

        DatabaseReference ref = database.getReference(MULTIPLAYER).child(key).child("guest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guest = dataSnapshot.getValue(User.class);

                if (TextUtils.isEmpty(guest.getFirstName())) {
                    guestFirstName = guest.getFirstName();
                } else {
                    guestName = guest.getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!this.isFinishing() && alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }


        if (alertDialog != null && alertDialog.isShowing()) {
            // do nothing here
        } else {
            // place all the AlertDialog builder here
            builder = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_cross)
                    .setTitle("Accept invitation")
                    // guest.getFirstName()
                    .setMessage(guest.getName() + " has invited you to join XvsO for an unforgettable battle")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // updates the acceptedRequest variable in the Firebase database
                            database.getReference("multiplayer").child(key).child("acceptedRequest").setValue(REQUEST_ACCEPTED);
                            startGame(key);
                            alertDialog.dismiss();
                        }
                        // (-) button
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "You have refused playing with this user", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            game.setStatus(Game.STATUS_WAITING);
                            database.getReference("multiplayer").child(key).child("status").setValue(Game.STATUS_WAITING);
                        }
                    });
            alertDialog = builder.create();
        }

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.btn_login));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.app_background));
            }
        });
    }
}
