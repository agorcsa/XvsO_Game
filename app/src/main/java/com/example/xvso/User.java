package com.example.xvso;

public class User {

    private String mFirstName;
    private String mLastName;
    private String mEmailAddress;
    private String mPassword;
    private String mImageUrl;

    private String mName;

    // empty constructor used for saving the user to database
    public User() {

    }

    public User(String name, String email, String password) {
        mName = name;
        
    }

    public User(String firstName, String lastName, String emailAddress, String password, String imageUrl) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = emailAddress;
        mPassword = password;
        mImageUrl = imageUrl;
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

    public String getPassword() { return mPassword;}

    public void setPassword(String mPassword) { this.mPassword = mPassword;  }

    public String getImageUrl() { return mImageUrl;}

    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}
