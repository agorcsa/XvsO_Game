package com.example.xvso.firebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.xvso.R;
import com.example.xvso.User;
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

import java.util.UUID;

import static com.example.xvso.MainActivity.LOG_TAG;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    // number of images to select
    private static final int PICK_IMAGE = 1;
    ActivityProfileBinding profileBinding;

    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private Uri imagePath;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    // used for checking if an upload is already running
    private StorageTask mUploadTask;

    private User newUser;

    private String fileName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        profileBinding.submitButton.setOnClickListener(this);
        profileBinding.profilePicture.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
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

            uploadUserImage();
        }
    }


    private void uploadUserImage() {

        if (imagePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            fileName = UUID.randomUUID().toString();
            final StorageReference storageReference = mStorageRef.child(getFirebaseUser().getUid()).child(fileName + "." + getFileExtension(imagePath));

            mUploadTask = storageReference.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            uploadImage();

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
        } else {
            showMessage("No image selected");
        }
    }

    public void uploadImage() {
        final StorageReference storageReference = mStorageRef.child(getFirebaseUser().getUid()).child(fileName + "." + getFileExtension(imagePath));

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                getEditTextData();

                newUser = new User(firstName, lastName, email, uri.toString());

                mDatabaseRef.child(getFirebaseUser().getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("Image successfully saved to database");

                        Glide.with(ProfileActivity.this)
                                .load(uri.toString())
                                .into(profileBinding.profilePicture);
                    }
                });

            }
        });
    }


    public void getEditTextData() {

        firstName = profileBinding.firstNameEditview.getText().toString();
        lastName = profileBinding.lastNameEditview.getText().toString();
        email = profileBinding.emailEditview.getText().toString();
    }

    public void updateUserData() {

       getEditTextData();

        if (imagePath != null) {

            String imageUrl = imagePath.toString();

            setDatabaseReference(imageUrl);

        } else {

            Uri uri = Uri.parse("android.resource://com.example.xvso.firebase/" + R.drawable.penguin);

            String penguinUrl = uri.toString();

            setDatabaseReference(penguinUrl);
        }
    }

    public void setDatabaseReference(String url) {

        User user = new User(firstName, lastName, email, url);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference mDbRef = mDatabase.getReference("users").child(getFirebaseUser().getUid());

        mDbRef.setValue(user);
    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    protected void onResume() {
        super.onResume();
        readFromDatabase();
    }

    private void readFromDatabase() {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/");

        // Read from the database
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.child(getFirebaseUser().getUid()).getValue(User.class);

                if (user != null) {
                    // picture part
                    String uri = user.getImageUrl();

                    Glide.with(getApplicationContext())
                            .load(Uri.parse(uri))
                            .into(profileBinding.profilePicture);
                    Log.d(LOG_TAG, "Value is: " + uri);

                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    String email = user.getEmailAddress();

                    profileBinding.firstNameEditview.setText(firstName);
                    profileBinding.lastNameEditview.setText(lastName);
                    profileBinding.emailEditview.setText(email);
                }
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
                    readFromDatabase();
                    updateUserProfile();
                    updateUserData();
                    showMessage("Changes have been saved");
                }
                break;
        }
    }


    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {

            showMessage("Edit profile");
        }

        return super.onOptionsItemSelected(item);
    }
}