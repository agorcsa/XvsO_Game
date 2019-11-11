package com.example.xvso.viewmodel;

import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private String firstNameVM;

    private String lastNameVM;

    private String emailVM;

    private String passwordVM;

    public String getFirstNameVM() {
        return firstNameVM;
    }

    public void setFirstNameVM(String firstNameVM) {
        this.firstNameVM = firstNameVM;
    }

    public String getLastNameVM() {
        return lastNameVM;
    }

    public void setLastNameVM(String lastNameVM) {
        this.lastNameVM = lastNameVM;
    }

    public String getEmailVM() {
        return emailVM;
    }

    public void setEmailVM(String emailVM) {
        this.emailVM = emailVM;
    }

    public String getPasswordVM() {
        return passwordVM;
    }

    public void setPasswordVM(String passwordVM) {
        this.passwordVM = passwordVM;
    }
}