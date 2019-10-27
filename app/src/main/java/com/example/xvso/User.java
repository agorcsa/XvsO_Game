package com.example.xvso;

public class User {

    private String mFirstName;
    private String mLastName;
    private String mEmailAddress;
    private String mImageUrl;

    // empty constructor used for saving the user to database
    public User() {

    }

    public User(String firstName, String lastName, String emailAddress, String imageUrl) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = emailAddress;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
