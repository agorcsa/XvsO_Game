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

    private LinearLayoutManager layoutManager;
    private GameAdapter gameAdapter;

    private User host;
    private User guest;

    private DatabaseReference query;

    private User currentUser;
    private User myUser = new User();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usersBinding = DataBindingUtil.setContentView(this, R.layout.activity_online_users);
        onlineUsersViewModel = ViewModelProviders.of(this).get(OnlineUsersViewModel.class);

        usersBinding.setViewModel(onlineUsersViewModel);
        usersBinding.setLifecycleOwner(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        buildRecyclerView();
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
                    userName = convertEmailToString(LoginUserID);

                    if (userNameCheck(true)) {
                            userName = userName.replace(".", "1");
                    }

                    myRef.child("users").child(LoginUID).child("request").setValue(LoginUID);

                    acceptIncomingRequests();
                } else {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out or login");
                    joinOnlineGame();
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

        myRef.child("playing").child(key).removeValue();
        Intent intent = new Intent(getApplicationContext(), OnlineGameActivity.class);
        intent.putExtra("player_session", key);
        intent.putExtra("user_name", userName);
        intent.putExtra("login_uid", LoginUID);
        startActivity(intent);
        finish();
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

    private String convertEmailToString(String email) {

        userName = email.substring(0, getFirebaseUser().getEmail().indexOf("@"));

        return userName;
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

    public void buildRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        gameAdapter = new GameAdapter(this, mOpenGamesList);
        usersBinding.gamesRecyclerView.setHasFixedSize(true);
        usersBinding.gamesRecyclerView.setLayoutManager(layoutManager);
        usersBinding.gamesRecyclerView.setAdapter(gameAdapter);
    }

    public void onNewGameButtonClicked(View view) {
        Toast.makeText(getApplicationContext(), "A new game has been created", Toast.LENGTH_LONG).show();
        addNewGame();
    }

    public void addNewGame() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        User guest = new User();

        if (firebaseUser != null) {
            Game game = new Game();
            game.setHost(myUser);
            game.setGuest(guest);
            LoginUID = firebaseUser.getUid();
            userName = convertEmailToString(LoginUserID);
            String key = myRef.getKey();
            game.setKey(key);
            DatabaseReference newGameRef = myRef.child(MULTIPLAYER).push();
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

        // Read from the "multiplayer" node from the database
        DatabaseReference ref = database.getReference(MULTIPLAYER);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mOpenGamesList.clear();

                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Game game = item.getValue(Game.class);
                    mOpenGamesList.add(game);
                }

                gameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /*public void startGame() {

        game.setStatus(Game.STATUS_PLAYING);

        Intent intent = new Intent(OnlineUsersActivity.this, OnlineGameActivity.class);
        intent.putExtra("gameID", LoginUID);
        intent.putExtra("guestID", (Parcelable) guest);
        startActivity(intent);
    }
*/
    public boolean userNameCheck(Boolean b) {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            LoginUserID = user.getEmail();
            userName = convertEmailToString(LoginUserID);

            b = userName.contains(".");

        }
        return b;
    }

    public void opponentJoinedGame() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("multiplayer").child(LoginUID).child("guest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User guest = dataSnapshot.getValue(User.class);

                if (guest != null) {
                    //startGame();
                    game.setStatus(Game.STATUS_PLAYING);
                } else {
                    game.setStatus(Game.STATUS_WAITING);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onJoinGameClick(String key) {
        startGame(key);
    }
}
