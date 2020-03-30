package com.example.xvso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private ActivityOnlineUsersBinding usersBinding;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;

    // triggered at user sign-in, sign-out, or change, or when the listener was registered
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<String> loggedUsersArrayList = new ArrayList();
    private ArrayAdapter loggedUsersArrayAdapter;

    private ListView requestedUsersListView;
    private ArrayList<String> requestedUsersArrayList = new ArrayList<>();
//    private ArrayAdapter requestedUsersArrayAdapter;

    private TextView userIdTextView;
    private String LoginUID;
    private String LoginUserID;
    private String userName;

    private Game game = new Game();

    private OnlineUsersViewModel onlineUsersViewModel;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private ArrayList<Game> mOpenGamesList = new ArrayList<>();
    private ArrayList<Game> mOpenGames = new ArrayList<>();

    private LinearLayoutManager layoutManager;
    private GameAdapter gameAdapter;

    private User host;
    private User guest;

    private DatabaseReference query;

    private User currentUser = new User();
    private User myUser = new User();

    private boolean newGame;
    private String key;

    private String guestFirstName;
    private String guestName;

    private TextView joinButton;

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
        readFromDatabase();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            // when the user will be changed
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    LoginUID = user.getUid();
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in: " + LoginUID);
                    LoginUserID = user.getEmail();
                    usersBinding.userLoginTextview.setText(LoginUserID);
                    userName = myUser.getName();

                    if (userNameCheck(true)) {
                        userName = userName.replace(".", "1");
                    }

                    myRef.child("users").child(LoginUID).child("request").setValue(LoginUID);

                    acceptIncomingRequests();
                } else {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out or login");
                    //joinOnlineGame();
                }
            }
        };

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
            }
        });

        gameAdapter.notifyDataSetChanged();
    }


    public void confirmRequest(final String otherPlayer, final String reqType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.connect_player_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Start Game");
        builder.setMessage("Connect with " + otherPlayer);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                myRef.child("users").child(otherPlayer).child("request").push().setValue(LoginUserID);

                if(reqType.equalsIgnoreCase("From")) {
                    //startGame(otherPlayer + ":" + userName, otherPlayer, "From:");
                } else {
                    //startGame(userName + ":" + otherPlayer, otherPlayer, "To");
                }
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void startGame(String key) {

        Intent intent = new Intent(getApplicationContext(), OnlineGameActivity.class);

        String playerSession = database.getReference("multiplayer").child(key).getKey();

        intent.putExtra(PLAYER_SESSION, playerSession);

        startActivity(intent);
        finish();
    }


    private void writeGuestToDatabase(User guest) {



        database.getReference("multiplayer").child(key).child("guest").setValue(myUser);
    }


    public void updateLoginUsers(DataSnapshot dataSnapshot) {

        String key = "";
        Set<String> set = new HashSet<>();

        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);

            if (user!= null) {
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

                            HashMap<String,Object> map = (HashMap<String, Object>)dataSnapshot.getValue();
                            if (map != null) {
                                String value = "";

                                for (String key:map.keySet()) {
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

    private void joinOnlineGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.login_dialog, null);
        builder.setView(dialogView);

        final EditText emailEditText = dialogView.findViewById(R.id.email_editview);
        final EditText passwordEditText = dialogView.findViewById(R.id.password_editview);

        builder.setTitle("Please register");
        builder.setMessage("Enter your email and password for registration");
        builder.setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // register user
                registerUser(emailEditText.getText().toString(), passwordEditText.getText().toString());

            }
        });
        builder.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth complete", "createUserWithEmail:success" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Auth failed", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void buildRecyclerView(User user) {

            layoutManager = new LinearLayoutManager(this);

            for (Game game : mOpenGames) {
                if (game.getStatus() == Game.STATUS_WAITING) {
                    mOpenGames.add(game);
                }
            }

            gameAdapter = new GameAdapter(this, mOpenGamesList, user);
            usersBinding.gamesRecyclerView.setHasFixedSize(true);
            usersBinding.gamesRecyclerView.setLayoutManager(layoutManager);
            usersBinding.gamesRecyclerView.setAdapter(gameAdapter);
        }


    public void addNewGame() {

        User guest = new User();

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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference(MULTIPLAYER);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mOpenGamesList.clear();

                for (DataSnapshot item: dataSnapshot.getChildren()) {
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
            //userName = convertEmailToString(LoginUserID);

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

    public void acceptedRequestListener() {

        DatabaseReference ref = database.getReference(MULTIPLAYER).child(key).child("acceptedRequest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer isRequestAccepted = dataSnapshot.getValue(Integer.class);

                if (isRequestAccepted == REQUEST_ACCEPTED) {
                    startGame(key);

                    game.setStatus(Game.STATUS_PLAYING);
                    database.getReference("multiplayer").child(key).child("status").setValue(Game.STATUS_PLAYING);

                } else {
                    // do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // alert dialog used when the guest sends a request to the host
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_cross)
                .setTitle("Accept invitation")
                // guest.getFirstName()
                .setMessage(guest.getName() + " has invited you to join XvsO for an unforgettable battle")

                // (+) button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // updates the acceptedRequest variable in the Firebase database
                        database.getReference("multiplayer").child(key).child("acceptedRequest").setValue(REQUEST_ACCEPTED);
                        DatabaseReference ref = database.getReference("multiplayer").child(key).child("guest");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                guest = dataSnapshot.getValue(User.class);
                                onJoinGameClick(key);
                                acceptedRequestListener();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
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

        AlertDialog alertDialog = builder.create();
        if (!this.isFinishing()) {
            alertDialog.show();
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
