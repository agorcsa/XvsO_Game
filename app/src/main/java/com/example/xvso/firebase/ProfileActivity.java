package com.example.xvso.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.xvso.R;
import com.example.xvso.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import static com.example.xvso.MainActivity.LOG_TAG;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    // number of images to select
    private static final int PICK_IMAGE = 1;
    ActivityProfileBinding profileBinding;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;
    private String fileName = "";
    private StorageReference mStorageRef;
    // used for checking if an upload is already running
    private StorageTask mUploadTask;
    private Uri imagePath;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        profileBinding.submitButton.setOnClickListener(this);
        profileBinding.profilePicture.setOnClickListener(this);
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imagePath = data.getData();

            uploadImage();
        }
    }


    private void uploadImage() {

        if (imagePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            fileName = UUID.randomUUID().toString();

            final StorageReference reference = mStorageRef.child("images/" + fileName);

            mUploadTask = reference.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {

                                    DatabaseReference imageStore = FirebaseDatabase.getInstance().getReference().child("images/");

                                    imageStore.setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showMessage("Image successfully saved to database");

                                            Glide.with(ProfileActivity.this)
                                                    .load(uri)
                                                    .into(profileBinding.profilePicture);
                                        }
                                    });
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //readFromDatabase();
    }

    private void readFromDatabase() {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("images/" + fileName);


        // Read from the database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String uri =  dataSnapshot.child("images/").getValue(String.class);

                Glide.with(ProfileActivity.this)
                        .load(Uri.parse(uri))
                        .into(profileBinding.profilePicture);
                Log.d(LOG_TAG, "Value is: " + uri);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(LOG_TAG, "Failed to read value.", error.toException());
            }
        });
    }


    public void updateUserProfile() {

        FirebaseUser user = getFirebaseUser();

        firstName = profileBinding.firstNameEditview.getText().toString();
        lastName = profileBinding.lastNameEditview.getText().toString();
        email = profileBinding.emailEditview.getText().toString();

         if (firstName.isEmpty()) {
           showMessage("Please introduce your first name");
         } else if (lastName.isEmpty()) {
             showMessage("Please introduce your last name");
         } else if (email.isEmpty()) {
             showMessage("Please introduce your e-mail address");
         } else {

             UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                     .setDisplayName(firstName + " " + lastName)
                     .build();

             user.updateProfile(profileUpdates)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()) {
                                 Log.d(LOG_TAG, "User profile updated.");
                             }
                         }
                     });
         }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_picture:
                selectImage();
                break;
            case R.id.submit_button:
                // in case the "submit" button is clicked more times for the same picture,
                // while the upload is already in progress
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload is already in progress", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile();
                    showMessage("Changes have been saved");
                }
                break;
        }
    }


    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}