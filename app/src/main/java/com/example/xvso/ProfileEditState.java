package com.example.xvso;

public class ProfileEditState {

    private boolean isProgressDialogShown;
    private double progressDialogPercentage;
    public ProfileEditState() {
    }
    public boolean isProgressDialogShown() {
        return isProgressDialogShown;
    }
    public void setProgressDialogShown(boolean progressDialogShown) {
        isProgressDialogShown = progressDialogShown;
    }
    public double getProgressDialogPercentage() {
        return progressDialogPercentage;
    }
    public void setProgressDialogPercentage(double progressDialogPercentage) {
        this.progressDialogPercentage = progressDialogPercentage;
    }
}
