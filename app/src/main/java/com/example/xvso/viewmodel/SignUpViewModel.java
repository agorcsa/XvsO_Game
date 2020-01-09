package com.example.xvso.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBar = new MutableLiveData<>(true);

    public SignUpViewModel() {

    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<Boolean> getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(MutableLiveData<Boolean> progressBar) {
        this.progressBar = progressBar;
    }

    public void hideProgressBar() {
        progressBar.postValue(false);
    }
}
