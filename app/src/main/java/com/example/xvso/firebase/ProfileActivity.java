package com.example.xvso.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.xvso.MainActivity;
import com.example.xvso.R;
import com.example.xvso.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kienht.csiv.CircleSliceImageView;


public class ProfileActivity extends BaseActivity {

    public static final String LOG_TAG = "ProfileActivity";

    // number of images to select
    private static final int PICK_IMAGE = 1;
    private static final String IMAGE_URI = "uri";
    private static final String SHARED_PREFERENCE = "image_uri";

    ActivityProfileBinding profileBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        String imageString = preferences.getString("image path", "");

            if (imageString != null) {
                Uri myUri = Uri.parse(imageString);
                profileBinding.profilePicture.setImageURI(myUri);
            } else {
                profileBinding.profilePicture.setImageURI(Uri.parse("https://i.pinimg.com/originals/92/fb/2e/92fb2e031df943d53008c3fa946662f3.jpg"));
            }
        }

   public void selectPicture(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            profileBinding.profilePicture.setImageURI(imageUri);

            String imagePath = String.valueOf(imageUri);

            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).edit();
            editor.putString("image path", imagePath);
            editor.apply();
        }
    }

    public void submitUserChanges(View view){
        String firstName = profileBinding.firstNameEditview.getText().toString();
        String lastName = profileBinding.lastNameEditview.getText().toString();
        String email = profileBinding.emailEditview.getText().toString();

        Intent userDataIntent = new Intent(ProfileActivity.this, MainActivity.class);
        userDataIntent.putExtra("firstName", firstName);
        userDataIntent.putExtra("lastName", lastName);
        userDataIntent.putExtra("email", email);
        startActivity(userDataIntent);
    }
}






