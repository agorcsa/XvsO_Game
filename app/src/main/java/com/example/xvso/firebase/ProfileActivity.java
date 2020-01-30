package com.example.xvso.firebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.xvso.ProfileEditState;
import com.example.xvso.R;
import com.example.xvso.databinding.ActivityProfileBinding;
import com.example.xvso.eventobserver.EventObserver;
import com.example.xvso.viewmodel.ProfileViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    public static final String LOG_TAG = "ProfileActivity";

    // number of images to select
    // only one image will be selected
    private static final int PICK_IMAGE = 1;

    // used to bind the views in activity_profile.xml through <layout>...</Layout>
    private ActivityProfileBinding profileBinding;

    // creates an instance of the ViewModel
    private ProfileViewModel profileViewModel;

    // creates an instance of the storage where we will store our user data
    private StorageReference mStorageRef;

    // creates an instance of the database where our user data will be stored
    private DatabaseReference mDatabaseRef;

    // used for checking if an upload is already running
    private StorageTask mUploadTask;

    // the path of the profile image that we are going to store
    private Uri imagePath;

    private ProgressDialog progressDialog;

    private ProfileEditState profileEditState;
    private ProfileViewModel.NetworkState networkState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // binds the activity_profile.xml with ProfileActivity.java
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        // the profileViewModel instance uses the ProfileViewModel.class
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        // sets onClickListener on the submitButton in order to be clickable and run some code
        profileBinding.submitButton.setOnClickListener(this);
        // sets onClickListener on the profilePicture in order to be clickable and run some code
        profileBinding.profilePicture.setOnClickListener(this);

        profileBinding.setViewModelProfile(profileViewModel);

        profileBinding.setLifecycleOwner(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        setupProgressDialog();
    }

    // called when we want to select a picture from the phone's gallery
    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    // if we have an intent and it's not null and contains data,
    // it takes out the data (picture Uri) from the intent and
    // uploads the picture as profile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && intent != null && intent.getData() != null) {

            // gets the imagePath (Uri) from the intent
            imagePath = intent.getData();

            // uploads the user's profile picture
            profileViewModel.uploadPicture(intent);
        }
    }

    // returns the type of a file extension
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    // used while clicking on the user's profile picture
    // used while clicking on the submit button
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
                    // readFromDatabase() - not needed anymore if we use ViewModel + MutableLiveData
                    // displays the the new profile picture
                    profileViewModel.updateUserProfile();
                    // displays the new user's data
                    profileViewModel.getUserDetailsFromDatabase();
                    // if all the input fields are valid, displays a Toast with them
                    if (profileViewModel.validateInputFields()) {
                        showMessage(profileViewModel.createInputText());
                    }
                }
                break;
        }
    }

    // auxiliary method for displaying a Toast message, by just giving the message we want to display
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.uploading_image));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
    }

    private void observeState() {
        profileViewModel.stateLiveData.observe(this, profileEditState -> {
            int progress = (int) Math.round(profileEditState.getProgressDialogPercentage());
            if (profileEditState.isProgressDialogShown()) {
                progressDialog.setProgress(progress);
                progressDialog.show();
            } else {
                progressDialog.hide();
            }
        });
    }

    private void observeStatus() {
        profileViewModel.networkState.observe(this, new EventObserver<>(this::handleStatus));
    }


    public void handleStatus(ProfileViewModel.NetworkState networkState) {
        switch (networkState) {
            case LOADED:
                showMessage("Loaded");
                break;
            case LOADING:
                showMessage("Loading");
                break;
            case UPLOADING:
                showMessage("Uploading");
            case FAILED:
                showMessage("Failed");
                break;
        }
    }
}