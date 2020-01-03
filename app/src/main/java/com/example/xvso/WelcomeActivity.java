package com.example.xvso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.xvso.databinding.ActivityWelcomeBinding;
import com.example.xvso.firebase.LoginActivity;

public class WelcomeActivity extends AppCompatActivity{

    private static final String LOG_TAG = "Welcome Screen";

    private ActivityWelcomeBinding welcomeBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        welcomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);

        welcomeBinding.singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        welcomeBinding.multiPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // assign a multi-player game session
            }
        });
    }
}
