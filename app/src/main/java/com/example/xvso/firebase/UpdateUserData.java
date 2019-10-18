package com.example.xvso.firebase;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xvso.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

public class UpdateUserData extends BaseActivity {

    public static final String LOG_TAG = "UpdateUserData";

    TextView userNameTextView;
    EditText userName;
    ImageView userImage;
    Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_data);

        userNameTextView = findViewById(R.id.user_name_text_view);
        userName = findViewById(R.id.user_name);
        userImage = findViewById(R.id.user_image);
        submitButton = findViewById(R.id.submit_button);
    }

    public void saveUserChanges(View view) {

        String stringUser = userName.getText().toString();

        userNameTextView.setText(stringUser);

        FirebaseUser user = getFirebaseUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(stringUser)
                .setPhotoUri(Uri.parse("https://i.pinimg.com/originals/92/fb/2e/92fb2e031df943d53008c3fa946662f3.jpg"))
                .build();

        if (user != null) {

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "User profile updated.");
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please sign-up first and only after update your profile", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(getApplicationContext(), "User changes saved", Toast.LENGTH_LONG).show();
    }
}
