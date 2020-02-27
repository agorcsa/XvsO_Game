package com.example.xvso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.xvso.databinding.ActivityOnlineGameBinding;
import com.example.xvso.viewmodel.ScoreViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlineGameActivity extends AppCompatActivity {

    private static final String LOG_TAG = "OnlineGameActivity";

    private ScoreViewModel mScoreViewModel;
    private ActivityOnlineGameBinding onlineGameBinding;

    private String playerSession = "";
    private String userName = "";
    private String otherPlayer = "";
    private String loginUID = "";
    private String requestType = "";
    private String myGameSignIn = "X";
    private int gameState = 0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    int activePlayer = 1;
    ArrayList<Integer> player1 = new ArrayList<>();
    ArrayList<Integer> player2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);

        onlineGameBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // gets the values from the intent
        userName = getIntent().getExtras().get("user_name").toString();
        loginUID = getIntent().getExtras().get("login_uid").toString();
        otherPlayer = getIntent().getExtras().get("other_player").toString();
        requestType = getIntent().getExtras().get("request_type").toString();
        playerSession = getIntent().getExtras().get("player_session").toString();

        gameState = 1;

        if (requestType.equals("From")) {

            myGameSignIn = "O";

            onlineGameBinding.player1Text.setText("Your turn");
            onlineGameBinding.player2Text.setText("Your turn");

            reference.child("playing").child(playerSession).child("turn").setValue(otherPlayer);

        } else {

            myGameSignIn = "X";

            onlineGameBinding.player1Text.setText(otherPlayer + "\'s turn");
            onlineGameBinding.player1Text.setText(otherPlayer + "\'s turn");

            reference.child("playing").child(playerSession).child("turn").setValue(otherPlayer);
        }

        reference.child("playing").child(playerSession).child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    String value = (String) dataSnapshot.getValue();
                    if (value.equals(userName)) {

                        onlineGameBinding.player1Text.setText("Your turn");
                        onlineGameBinding.player2Text.setText("Your turn");
                        setEnableClick(true);
                        activePlayer = 1;
                    } else if (value.equals(otherPlayer)) {

                        onlineGameBinding.player1Text.setText(otherPlayer + "\'s turn");
                        onlineGameBinding.player1Text.setText(otherPlayer + "\'s turn");
                        setEnableClick(false);
                        activePlayer = 2;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("playing").child(playerSession).child("game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // TO DO
                    onlineGameBinding.player1Result.clearComposingText();
                    onlineGameBinding.player2Result.clearComposingText();
                    activePlayer = 2;
                    HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        String value = "";
                        String firstPlayer = userName;
                        for (String key:map.keySet()) {
                            value = (String) map.get(key);
                            if (value.equals(userName)) {
                                activePlayer = 2;
                            } else {
                                activePlayer = 1;
                            }
                            firstPlayer = value;
                            String[] splitID = key.split(":");
                            // TO DO
                            //otherPlayer.(Integer.parseInt(splitID[1]));
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

    public void gameBoardClick(View view) {

        ImageView selectedImage = (ImageView) view;

        if (playerSession.length() <= 0) {
            Intent intent = new Intent(getApplicationContext(), OnlineGameActivity.class);
            startActivity(intent);
            finish();
        } else {
            int selectedBlock = 0;
            switch (selectedImage.getId()) {
                case R.id.block_1:
                    selectedBlock = 1;
                    break;
                case R.id.block_2:
                    selectedBlock = 2;
                    break;
                case R.id.block_3:
                    selectedBlock = 3;
                    break;

                case R.id.block_4:
                    selectedBlock = 4;
                    break;
                case R.id.block_5:
                    selectedBlock = 5;
                    break;
                case R.id.block_6:
                    selectedBlock = 6;
                    break;

                case R.id.block_7:
                    selectedBlock = 7;
                    break;
                case R.id.block_8:
                    selectedBlock = 8;
                    break;
                case R.id.block_9:
                    selectedBlock = 9;
                    break;
            }
            reference.child("playing").child(playerSession).child("game").child("block" + selectedBlock).setValue(userName);
            reference.child("playing").child(playerSession).child(playerSession).child("turn").setValue(otherPlayer);
            setEnableClick(false);
            activePlayer = 2;

            playGame(selectedBlock, selectedImage);
        }
    }

    public void playGame(int selectedBlock, ImageView selectedImage) {

        if (gameState == 1) {
            if (activePlayer == 1) {
                selectedImage.setImageResource(R.drawable.ic_cross);
                player1.add(selectedBlock);
            } else if (activePlayer == 2) {
                selectedImage.setImageResource(R.drawable.ic_zero);
                player2.add(selectedBlock);
            }
            selectedImage.setEnabled(false);
            checkWinner();
        }
    }

    public void checkWinner() {

        // no winner
        int winner = 0;

    }

    public void setEnableClick(boolean b) {

        onlineGameBinding.block1.setClickable(b);
        onlineGameBinding.block2.setClickable(b);
        onlineGameBinding.block3.setClickable(b);

        onlineGameBinding.block4.setClickable(b);
        onlineGameBinding.block5.setClickable(b);
        onlineGameBinding.block6.setClickable(b);

        onlineGameBinding.block7.setClickable(b);
        onlineGameBinding.block8.setClickable(b);
        onlineGameBinding.block9.setClickable(b);
    }
}
