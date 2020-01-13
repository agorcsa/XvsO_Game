package com.example.xvso.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xvso.User;

public class ProfileViewModel extends ViewModel {

    private User user = new User();
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private boolean isValid;

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

        if (!user.isFirstNameValid() || !user.isLastNameValid() || !user.isEmailValid() || !user.isPasswordValid()) {

            isFirstNameValid.setValue(false);
            isLastNameValid.setValue(false);
            isEmailValid.setValue(false);
            isPasswordValid.setValue(false);

            isValid = false;

        } else {
            isFirstNameValid.setValue(true);
            isLastNameValid.setValue(true);
            isEmailValid.setValue(true);
            isPasswordValid.setValue(true);
        }

        return true;
    }

    private boolean validateFirstName() {

        if (!user.isFirstNameValid()) {
            isFirstNameValid.setValue(false);
            isValid = false;
        } else {
            isFirstNameValid.setValue(true);
        }
        return true;
    }


    private boolean validateLastName() {

        if (!user.isLastNameValid()) {
            isLastNameValid.setValue(false);
            isValid = false;
        } else {
            isLastNameValid.setValue(true);
        }
        return true;
    }

    private boolean validateEmail() {

        if (!user.isEmailValid()) {
            isEmailValid.setValue(false);
            isValid = false;
        } else {
            isEmailValid.setValue(true);
        }
        return true;
    }


    private boolean validatePassword() {

        if (!user.isPasswordValid()) {
            isPasswordValid.setValue(false);
            isValid = false;
        } else {
            isPasswordValid.setValue(true);
        }
        return true;
    }

    private boolean confirmInput() {
        if (!validateFirstName() | !validateLastName() | !validateEmail() | !validatePassword()) {
            return false;
        }

        String input = "First name: " + getFirstName();
        input += "\n";
        input += "Last name: " + getLastName();
        input += "\n";
        input += "Email: " + getEmail();
        input += "\n";
        input += "Password: " + getPassword();

        //showMessage(input);

        return true;
    }
}
