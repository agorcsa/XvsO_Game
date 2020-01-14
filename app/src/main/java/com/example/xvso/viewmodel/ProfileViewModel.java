package com.example.xvso.viewmodel;

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

public class ProfileViewModel extends ViewModel {

    private static final String LOG_TAG = "ProfileViewModel";

    private User user = new User();
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private MutableLiveData<Boolean> isFirstNameValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isLastNameValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isEmailValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> isPasswordValid = new MutableLiveData<>(true);

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

    public User getUser() {
        return user;
    }

    private boolean validateInputFields() {

        boolean isValid = true;

        if (!user.isFirstNameValid()) {
            // error
            isFirstNameValid.setValue(false);
            isValid = false;
            return isValid;
        } else {
            // no error
            isFirstNameValid.setValue(true);
        }

        if (!user.isLastNameValid()) {
            // error
            isLastNameValid.setValue(false);
            isValid = false;
            return isValid;
        } else {
            // no error
            isLastNameValid.setValue(true);
        }

        if (!user.isEmailValid()) {
            // error
            isEmailValid.setValue(false);
            isValid = false;
            return isValid;
        } else {
            // no error
            isEmailValid.setValue(true);
        }

        if (!user.isPasswordValid()) {
            // error
            isPasswordValid.setValue(false);
            isValid = false;
            return isValid;
        } else {
            // no error
            isPasswordValid.setValue(true);
        }
        // if only one of the above fields fails to validate, it prevents us from sending the data to the database
        return false;
    }

    public boolean confirmInput() {
        if (!validateInputFields()) {
            return false;
        }

        /*String input = "First name: " + getFirstName();
        input += "\n";
        input += "Last name: " + getLastName();
        input += "\n";
        input += "Email: " + getEmail();
        input += "\n";
        input += "Password: " + getPassword();*/

        //showMessage(input);

        return true;
    }

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


    public void updateUserProfile() {

        //FirebaseUser user = getFirebaseUser();

        // 2. getEditTextData();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

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
