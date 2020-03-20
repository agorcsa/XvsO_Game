package com.example.xvso.Objects;

import android.util.Patterns;

public class User {

    private String mUID = "";
    private String mName = "";
    private String mFirstName = "";
    private String mLastName = "";
    private String mEmailAddress = "";
    private String mPassword = "";
    private String mImageUrl = "";
    private int mTotalScore = 0;

    private String picture;
    private int gameNumber;
    private String userName;

    private Boolean mActiveGame;

    // empty constructor used for saving the user to database
    public User() {

    }

    public User (String picture, int gameNumber, String userName) {
        this.picture = picture;
        this.gameNumber = gameNumber;
        this.userName = userName;
    }

    public User(String uid, String name, String email, String password) {
        mUID = uid;
        mName = name;
        mEmailAddress = email;
        mPassword = password;
    }

    public User(String firstName, String lastName, String email, String password, int totalScore) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = email;
        mPassword = password;
        mTotalScore = totalScore;
    }
    public User(String firstName, String lastName, String emailAddress, String password, String imageUrl, int totalScore) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = emailAddress;
        mPassword = password;
        mImageUrl = imageUrl;
        mTotalScore = totalScore;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUID() {
        return mUID;
    }

    public void setUID(String mUID) {
        this.mUID = mUID;
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

    public Boolean getActiveGame() {
        return mActiveGame;
    }

    public void setActiveGame(Boolean mActiveGame) {
        this.mActiveGame = mActiveGame;
    }

    public boolean isFirstNameValid() {
        if (!getFirstName().isEmpty() || isFieldLengthGreaterThan(getFirstName(), 10)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastNameValid() {
        if (!getLastName().isEmpty() || isFieldLengthGreaterThan(getLastName(), 10)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getEmailAddress()).matches();
    }

    public boolean isPasswordValid() {

        if (!getPassword().isEmpty() || isFieldLengthGreaterThan(getPassword(), 5)) {
            return true;
        } else {
            return false;
        }
    }

    // generic method for checking the length is greater than a given number
    public boolean isFieldLengthGreaterThan(String field, int number) {
        if (field.length() > number) {
            return true;
        } else {
            return false;
        }
    }
}
