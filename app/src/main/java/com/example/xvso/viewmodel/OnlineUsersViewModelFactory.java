package com.example.xvso.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class OnlineUsersViewModelFactory implements ViewModelProvider.Factory{

    public OnlineUsersViewModelFactory(String gameID) {
        this.gameID = gameID;
    }

    private String gameID;

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new OnlineGameViewModel(gameID);
    }
}
