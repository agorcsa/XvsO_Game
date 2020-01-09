package com.example.xvso.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.xvso.MainActivity;
import com.example.xvso.R;
import com.example.xvso.User;
import com.example.xvso.databinding.ActivitySignupBinding;
import com.example.xvso.viewmodel.SignUpViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity {

    private ActivitySignupBinding signupBinding;
    private SignUpViewModel signUpViewModel;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +           // beginning of the String
                    "(?=.*[0-9])" +       // at least 1 digit
                    "(?=.*[a-z])" +       // at least 1 lower case letter
                    //"(?=.*[A-Z])" +       // at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      // any letter (upper or lower case)
                    //"(?=.*[@#$%^&+=])" +    // at least 1 special character
                    //"(?=\\S+$)" +           // no white spaces
                    //".{6,}" +             // at least 6 characters
                    "$");                   // end of the String

    private FirebaseAuth auth;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        signUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        signupBinding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        signupBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signupBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             signUpViewModel.getEmail().setValue(signupBinding.inputEmail.getText().toString().trim());
             signUpViewModel.getPassword().setValue(signupBinding.inputPassword.getText().toString().trim());

                if (TextUtils.isEmpty((CharSequence) signUpViewModel.getEmail())) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;

                } else if (!Patterns.EMAIL_ADDRESS.matcher((CharSequence) signUpViewModel.getEmail()).matches()) {
                    signupBinding.inputEmail.setError("Please enter a valid e-mail address");
                }

                if (TextUtils.isEmpty((CharSequence) signUpViewModel.getPassword())) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (((CharSequence) signUpViewModel.getPassword()).length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                signupBinding.progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(signUpViewModel.getEmail().toString(), signUpViewModel.getPassword().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                signupBinding.progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    String name = getFirebaseUser().getEmail().substring(0, getFirebaseUser().getEmail().indexOf("@"));
                                    String firstName = "";
                                    String lastName = "";

                                    User user = new User(name, signUpViewModel.getEmail(), signUpViewModel.getPassword());

                                    databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                    databaseReference.child(getFirebaseUser().getUid()).setValue(user);

                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        signupBinding.progressBar.setVisibility(View.GONE);
    }
}
