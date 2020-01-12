package com.example.xvso.firebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.xvso.R;
import com.example.xvso.User;
import com.example.xvso.databinding.ActivityProfileBinding;
import com.example.xvso.viewmodel.ProfileViewModel;
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

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    public static final String LOG_TAG = "ProfileActivity";

    // number of images to select
    private static final int PICK_IMAGE = 1;

    ActivityProfileBinding profileBinding;

    private ProfileViewModel profileViewModel;

    // represents the substring of the email address, the first part before "@"
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Uri imagePath;
    private String imageUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    // used for checking if an upload is already running
    private StorageTask mUploadTask;

    private User globalUser;

    private String fileName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        profileBinding.submitButton.setOnClickListener(this);
        profileBinding.profilePicture.setOnClickListener(this);

        profileBinding.setViewModelProfile(profileViewModel);

        profileBinding.setLifecycleOwner(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        // not needed, because at first log-in the user is empty
        //globalUser = new User(firstName, lastName, email, password, imageUrl);
        globalUser = new User();
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

            imageUrl = imagePath.toString();

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

                // 2. getEditTextData();

                //globalUser = new User(firstName, lastName, email, password, imageUrl);

                globalUser.setImageUrl(uri.toString());

                mDatabaseRef.child(getFirebaseUser().getUid()).setValue(globalUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("Image successfully saved to database");

                        if (globalUser.getImageUrl() != null) {

                            Glide.with(getApplicationContext())
                                    .load(globalUser.getImageUrl())
                                    .apply(new RequestOptions().error(R.drawable.tictactoe))
                                    .into(profileBinding.profilePicture);
                        }
                    }
                });
            }
        });
    }

    /*private void setEditTextData(String firstName, String lastName, String password, String email) {
        profileBinding.firstNameEditview.setText(firstName);
        profileBinding.lastNameEditview.setText(lastName);
        profileBinding.passwordEditview.setText(password);
        profileBinding.emailEditview.setText(email);
    }*/


    /*public void getEditTextData() {
        firstName = profileBinding.firstNameEditview.getText().toString();
        lastName = profileBinding.lastNameEditview.getText().toString();
        password = profileBinding.passwordEditview.getText().toString();
        email = profileBinding.emailEditview.getText().toString();
    }*/

    public void updateUserData() {

        // 2. getEditTextData();

        if (imagePath != null) {

            String imageUrl = imagePath.toString();

            setDatabaseReference(imageUrl);

        } else {

            Uri uri = Uri.parse("android.resource://com.example.xvso.firebase/" + R.drawable.tictactoe);

            String placeholderUrl = uri.toString();

            setDatabaseReference(placeholderUrl);
        }
    }

    public void setDatabaseReference(String url) {

        globalUser.setFirstName(firstName);

        globalUser.setLastName(lastName);

        globalUser.setPassword(password);

        globalUser.setEmailAddress(email);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference mDbRef = mDatabase.getReference("users").child(getFirebaseUser().getUid());

        mDbRef.setValue(globalUser);
    }


    private String getFileExtension(Uri uri) {
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
                if (getFirebaseUser() != null) {

                    globalUser = dataSnapshot.child(getFirebaseUser().getUid()).getValue(User.class);

                    if (globalUser != null) {
                        // picture part
                        String uri = globalUser.getImageUrl();

                        if (globalUser.getImageUrl() != null) {

                            Glide.with(getApplicationContext())
                                    .load(globalUser.getImageUrl())
                                    .apply(new RequestOptions().error(R.drawable.tictactoe))
                                    .into(profileBinding.profilePicture);

                            Log.d(LOG_TAG, "Value is: " + uri);

                            String firstName = globalUser.getFirstName();
                            String lastName = globalUser.getLastName();
                            String password = globalUser.getPassword();
                            String email = globalUser.getEmailAddress();

                            // 1. setEditTextData(firstName, lastName, password, email);

                            String fullName = firstName + " " + lastName;

                            profileBinding.userNameTextview.setText(fullName);
                            profileBinding.emailAddressTextview.setText(email);

                        } else {

                            Glide.with(getApplicationContext())
                                    .load(R.drawable.tictactoe)
                                    .into(profileBinding.profilePicture);

                            if (TextUtils.isEmpty(globalUser.getFirstName())) {
                                firstName = "";
                            } else {
                                firstName = globalUser.getFirstName();
                            }

                            if (TextUtils.isEmpty(globalUser.getFirstName())) {
                                lastName = "";
                            } else {
                                lastName = globalUser.getLastName();
                            }

                            String password = globalUser.getPassword();
                            String email = globalUser.getEmailAddress();

                            // 1. setEditTextData(firstName, lastName, password, email);

                            String fullName = firstName + " " + lastName;

                            profileBinding.userNameTextview.setText(fullName);
                            profileBinding.emailAddressTextview.setText(email);
                        }

                    } else {

                        Glide.with(getApplicationContext())
                                .load(R.drawable.tictactoe)
                                .into(profileBinding.profilePicture);
                    }
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

        // 2. getEditTextData();

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
                    // readFromDatabase();
                    updateUserProfile();
                    updateUserData();
                    /*if (confirmInput()) {
                        showMessage("Changes have been saved");
                    }*/
                }
                break;
        }
    }

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}