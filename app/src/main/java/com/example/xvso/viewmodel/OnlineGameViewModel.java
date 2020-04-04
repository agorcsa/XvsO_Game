package com.example.xvso.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class OnlineGameViewModel extends BaseViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private MutableLiveData<ArrayList<Integer>> boardLiveData = new MutableLiveData<>();

    private String gameID = "";

    public OnlineGameViewModel(String gameID) {
        this.gameID = gameID;
    }
}
