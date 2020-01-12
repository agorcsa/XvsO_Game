package com.example.xvso;

import android.util.Patterns;

public class User {

    private String mName = "";
    private String mFirstName = "";
    private String mLastName = "";
    private String mEmailAddress = "";
    private String mPassword = "";
    private String mImageUrl = "";

    // empty constructor used for saving the user to database
   public User() {

   }

    public User(String name, String email, String password) {
        mName = name;
        mEmailAddress =  email;
        mPassword = password;
    }

    public User(String firstName, String lastName, String email, String password) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = email;
        mPassword = password;
    }

    public User(String firstName, String lastName, String emailAddress, String password, String imageUrl) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = emailAddress;
        mPassword = password;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public boolean isFirstNameValid() {
       if (!isFirstNameEmpty() || isFirstNameGreaterThanTen()) {
           return true;
       } else {
           return false;
       }
    }

    public boolean isFirstNameEmpty() {
       if (getFirstName().isEmpty()) {
            return true;
        } else {
           return false;
       }
    }

    public boolean isFirstNameGreaterThanTen() {
       if (getFirstName().length() > 10) {
           return true;
       } else {
           return false;
       }
    }

    public boolean isLastNameValid() {
        if (!isLastNameEmpty() || isLastNameGreaterThanTen()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastNameEmpty() {
        if (getLastName().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastNameGreaterThanTen() {
        if (getLastName().length() > 10) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getEmailAddress()).matches();
    }

    public boolean isPasswordValid() {

       if (!isPasswordEmpty() || isPasswordLengthGraterThanFive()) {
           return true;
       } else {
           return false;
       }
    }

    public boolean isPasswordLengthGraterThanFive() {
        if (getPassword().length() > 5) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPasswordEmpty() {
       if (getPassword().isEmpty()) {
           return true;
       } else {
           return false;
       }
    }
}
