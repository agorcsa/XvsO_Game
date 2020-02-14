package com.example.xvso.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xvso.ProfileEditState;
import com.example.xvso.ResourceProvider;
import com.example.xvso.User;
import com.example.xvso.eventobserver.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class ProfileViewModel extends ViewModel {

    private static final String LOG_TAG = "ProfileViewModel";

    private MutableLiveData<Event<NetworkState>> networkState = new MutableLiveData<>();

    public void setNetworkState(MutableLiveData<Event<NetworkState>> networkState) {
        this.networkState = networkState;
    }

    public MutableLiveData<Event<NetworkState>> getNetworkState() {
        return networkState;
    }

    public MutableLiveData<ProfileEditState> stateLiveData = new MutableLiveData<>();

    private ResourceProvider resourceProvider;

    private User user = new User();
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();

    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String imageUrl;
    private String fileName = "";

    // MuatableLiveData variables for validating all 4 fields
    private MutableLiveData<Boolean> isFirstNameValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isLastNameValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isEmailValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isPasswordValid = new MutableLiveData<>(true);

    // Firebase variables
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private ProfileEditState profileEditState;

    // constructor
    public ProfileViewModel() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference("users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        profileEditState = new ProfileEditState();
        stateLiveData.setValue(profileEditState);

        getUserDetailsFromDatabase();
    }

    public MutableLiveData<ProfileEditState> getStateLiveData() {
        return stateLiveData;
    }

    public void setStateLiveData(MutableLiveData<ProfileEditState> stateLiveData) {
        this.stateLiveData = stateLiveData;
    }

    // getters and setters of the 4 MutableLiveData Boolean variables
    public MutableLiveData<Boolean> getIsFirstNameValid() {
        return isFirstNameValid;
    }

    public MutableLiveData<Boolean> getIsLastNameValid() {
        return isLastNameValid;
    }

    public MutableLiveData<Boolean> getIsPasswordValid() {
        return isPasswordValid;
    }

    public MutableLiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<Boolean> getIsEmailValid() {
        return isEmailValid;
    }

    public void setIsEmailValid(MutableLiveData<Boolean> isEmailValid) {
        this.isEmailValid = isEmailValid;
    }

    // getters and setters of the 4 String fields variables
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // returns true if all the 4 String fields are valid
    // returns false
    public boolean validateInputFields() {

        boolean isValid = true;

        if (!user.isFirstNameValid()) {
            isFirstNameValid.setValue(false);
            isValid = false;

        } else {
            isFirstNameValid.setValue(true);
        }

        if (!user.isLastNameValid()) {
            isLastNameValid.setValue(false);
            isValid = false;

        } else {
            isLastNameValid.setValue(true);
        }

        if (!user.isEmailValid()) {
            isEmailValid.setValue(false);
            isValid = false;

        } else {
            isEmailValid.setValue(true);
        }

        if (!user.isPasswordValid()) {
            isPasswordValid.setValue(false);
            isValid = false;

        } else {
            isPasswordValid.setValue(true);
        }

        // if only one of the above fields fails to validate, it prevents us from sending the data to the database
        return isValid;
    }


    private void saveUserToDatabase() {

            mDatabaseRef
                    .child(firebaseUser.getUid())
                    .setValue(user)
                    .addOnSuccessListener(aVoid ->
                            networkState.setValue(new Event<>(NetworkState.LOADED)))
                    .addOnFailureListener(aVoid ->
                            networkState.setValue(new Event<>(NetworkState.FAILED)))
            ;
    }




    private void saveImageUrlInDatabase(Uri uri) {
        user.setImageUrl(uri.toString());
        mDatabaseRef
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnSuccessListener(aVoid ->
                        networkState.setValue(new Event<>(NetworkState.IMAGE_UPLOADED)))
                .addOnFailureListener(aVoid ->
                        networkState.setValue(new Event<>(NetworkState.FAILED)))
        ;
    }

    public void uploadPicture(Intent intentData) {
        Uri imagePath = intentData.getData();
        String fileName = UUID.randomUUID().toString();
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReference("users")
                .child(firebaseUser.getUid())
                .child(fileName);
        UploadTask uploadTask;
        if (imagePath != null) {
            uploadTask = photoRef.putFile(imagePath);
            uploadTask.addOnSuccessListener(taskSnapshot -> {

                networkState.setValue(new Event<>(NetworkState.LOADED));

                final StorageReference storageReference =
                        mStorageRef
                                .child(firebaseUser.getUid())
                                .child(fileName);
                storageReference.getDownloadUrl().addOnSuccessListener(this::saveImageUrlInDatabase);
                profileEditState.setProgressDialogShown(false);
                stateLiveData.setValue(profileEditState);
            });
            uploadTask.addOnFailureListener(e -> {

                networkState.setValue(new Event<>(NetworkState.FAILED));

                profileEditState.setProgressDialogShown(false);
                stateLiveData.setValue(profileEditState);
            });
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                profileEditState.setProgressDialogShown(true);
                profileEditState.setProgressDialogPercentage(progress);
                stateLiveData.setValue(profileEditState);
            });
        }
    }

    public void updateUserProfile() {

        // 2. getEditTextData();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "User profile updated.");
                        }
                    }
                });
    }

    public void submitForm() {
        //userLiveData.setValue(user);
        if (user != null && validateInputFields()) {

            user.setFirstName(userLiveData.getValue().getFirstName());
            user.setLastName((userLiveData.getValue().getLastName()));
            user.setEmailAddress(userLiveData.getValue().getEmailAddress());
            user.setPassword(userLiveData.getValue().getPassword());

            if (validateInputFields()) {
                saveUserToDatabase();
                //createInputText();
            }
        }
    }

    public void getUserDetailsFromDatabase() {

        mDatabaseRef
                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            userLiveData.setValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public enum NetworkState {
        IMAGE_UPLOADED,
        LOADED,
        LOADING,
        UPLOADING,
        FAILED
    }
}
