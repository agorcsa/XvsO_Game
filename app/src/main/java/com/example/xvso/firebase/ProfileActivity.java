package com.example.xvso.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.xvso.R;
import com.example.xvso.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kienht.csiv.CircleSliceImageView;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    public static final String LOG_TAG = "ProfileActivity";

    // number of images to select
    private static final int PICK_IMAGE = 1;

    private ActivityProfileBinding profileBinding;

    private StorageReference mStorageRef;

    // used for checking if an upload is already running
    private StorageTask mUploadTask;

    public Uri imagePath;

    private CircleSliceImageView profilePicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        profileBinding.submitButton.setOnClickListener(this);
        profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(this);

    }

    public void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imagePath = data.getData();

            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.penguin)
                    .error(R.drawable.error)
                    .into(profileBinding.profilePicture);
        }
    }


    public void uploadImage() {

        if (imagePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference reference = mStorageRef.child("images/" + UUID.randomUUID().toString());

           mUploadTask = reference.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
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
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void saveImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Uri downloadURI = mStorageRef.child("images/" + UUID.randomUUID().toString()).getDownloadUrl().getResult();

        Glide.with(ProfileActivity.this)
                .load(downloadURI)
                .into(profilePicture);
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
                    uploadImage();
                }
                break;
        }
    }
}






