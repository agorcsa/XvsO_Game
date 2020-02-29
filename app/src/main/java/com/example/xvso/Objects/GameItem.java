package com.example.xvso.Objects;

public class GameItem {

    private int mProfilePicture;
    private String mGameNumber;
    private String mLoggedUserName;

    public GameItem(int mProfilePicture, String mGameNumber, String mLoggedUserName) {
        this.mProfilePicture = mProfilePicture;
        this.mGameNumber = mGameNumber;
        this.mLoggedUserName = mLoggedUserName;
    }

    public int getProfilePicture() {
        return mProfilePicture;
    }

    public String getGameNumber() {
        return mGameNumber;
    }

    public String getLoggedUserName() {
        return mLoggedUserName;
    }
}
