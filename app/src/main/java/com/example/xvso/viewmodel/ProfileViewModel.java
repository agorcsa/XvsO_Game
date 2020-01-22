package com.example.xvso.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xvso.User;
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

import java.util.UUID;

public class ProfileViewModel extends ViewModel {

    private static final String LOG_TAG = "ProfileViewModel";

    private User user = new User();
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();

    // represents the substring of the email address, the first part before "@"
    private String name;
    // the 4 fields variables
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

    // constructor
    public ProfileViewModel() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference("users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
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

    // getter of the User object
    public User getUser() {
        return user;
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


    // creates a String from the user's data
    // used to display a Toast message if fields are validated
    public String createInputText() {

        String input = "First name: " + getFirstName();
        input += "\n";
        input += "Last name: " + getLastName();
        input += "\n";
        input += "Email: " + getEmail();
        input += "\n";
        input += "Password: " + getPassword();

        return input;
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
                networkState.setValue(NetworkState.LOADED);
                final StorageReference storageReference =
                        mStorageRef
                                .child(firebaseUser.getUid())
                                .child(fileName);
                storageReference.getDownloadUrl().addOnSuccessListener(this::saveImageUrlInDatabase);
                profileEditState.setProgressDialogShown(false);
                stateMutableLiveData.setValue(profileEditState);
            });
            uploadTask.addOnFailureListener(e -> {
                networkState.setValue(NetworkState.FAILED);
                profileEditState.setProgressDialogShown(false);
                stateMutableLiveData.setValue(profileEditState);
            });
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                profileEditState.setProgressDialogShown(true);
                profileEditState.setProgressDialogPercentage(progress);
                stateMutableLiveData.setValue(profileEditState);
            });
        }
    }


    public void updateUserProfile() {

        // 2. getEditTextData();

        // firebaseAuth = FirebaseAuth.getInstance();
        // firebaseUser = firebaseAuth.getCurrentUser();

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


    private void readFromDatabase() {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/");

        // Read from the database
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (firebaseUser != null) {

                    user = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);

                    if (user != null) {
                        // picture part
                        String uri = user.getImageUrl();

                        if (user.getImageUrl() != null) {

                            Log.d(LOG_TAG, "Value is: " + uri);

                            String firstName = user.getFirstName();
                            String lastName = user.getLastName();
                            String password = user.getPassword();
                            String email = user.getEmailAddress();

                            // TO DO from the xml
                            // setEditTextData(firstName, lastName, password, email);

                            String fullName = firstName + " " + lastName;

                            // TO DO from the xml
                            //profileBinding.userNameTextview.setText(fullName);
                            //profileBinding.emailAddressTextview.setText(email);

                        } else {

                            if (TextUtils.isEmpty(user.getFirstName())) {
                                firstName = "";
                            } else {
                                firstName = user.getFirstName();
                            }

                            if (TextUtils.isEmpty(user.getFirstName())) {
                                lastName = "";
                            } else {
                                lastName = user.getLastName();
                            }

                            String password = user.getPassword();
                            String email = user.getEmailAddress();

                            // // TO DO from the xml
                            // setEditTextData(firstName, lastName, password, email);

                            String fullName = firstName + " " + lastName;

                            // TO DO from the xml
                            //profileBinding.userNameTextview.setText(fullName);
                            //profileBinding.emailAddressTextview.setText(email);
                        }

                    } else {

                        // TO DO: replace Glide
                        /*Glide.with(getApplicationContext())
                                .load(R.drawable.tictactoe)
                                .into(profileBinding.profilePicture);*/
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
}
