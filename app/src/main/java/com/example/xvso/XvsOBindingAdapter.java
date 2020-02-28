package com.example.xvso;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.xvso.Objects.Team;
import com.example.xvso.viewmodel.OnlineUsersViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class XvsOBindingAdapter {

    public static final String PLEASE_WAIT = "Please wait...";
    public static final String CONNECTED = "Connected";

    @BindingAdapter("state")
    public static void setCellState(ImageView imageView, int state) {
        if (state == Team.TEAM_O) {
            // set image O
            imageView.setImageResource(R.drawable.ic_zero);
            // set clickable false
            imageView.setClickable(false);
        } else if (state == Team.TEAM_X) {
            // set image X
            imageView.setImageResource(R.drawable.ic_cross);
            // set clickable false
            imageView.setClickable(false);
        } else {
            // set no image
            imageView.setImageResource(0);
            // set clickable true
            imageView.setClickable(true);
        }
    }

    @BindingAdapter("isGameInProgress")
    public static void checkGameInProgress(ImageView imageView, boolean isGameInProgress) {
        if (isGameInProgress) {
            imageView.setClickable(true);
        } else {
            imageView.setClickable(false);
        }
    }

    @BindingAdapter({"name", "firstName"})
    public static void displayUserName(TextView textView, String name, String firstName) {
        if (TextUtils.isEmpty(firstName)) {
            textView.setText(name);
        } else {
            textView.setText(firstName);
        }
    }

    @BindingAdapter("errorFirstName")
    public static void errorFirstName(TextInputEditText view, Boolean isValid) {
        if (isValid) {
            view.setError(null);
        } else if (view.getText().toString().isEmpty()) {
            view.setError(view.getContext().getString(R.string.invalid_field));
        } else if (view.getText().length() > 10) {
            view.setError(view.getContext().getString(R.string.first_name_too_long));
        }
    }

    @BindingAdapter("errorLastName")
    public static void errorLastName(TextInputEditText view, Boolean isValid) {
        if (isValid) {
            view.setError(null);
        } else if (view.getText().toString().isEmpty()) {
            view.setError(view.getContext().getString(R.string.invalid_field));
        } else if (view.getText().length() > 10) {
            view.setError(view.getContext().getString(R.string.last_name_too_long));
        }
    }

    @BindingAdapter("errorEmail")
    public static void errorEmail(TextInputEditText view, Boolean isValid) {
        if (isValid) {
            // hide the error
            view.setError(null);
        } else if (view.getText().toString().isEmpty()) {
            // show the error
            view.setError(view.getContext().getString(R.string.invalid_field));
        }
    }

    @BindingAdapter("errorPassword")
    public static void errorPassword(TextInputEditText view, Boolean isValid) {
        if (isValid) {
            // hide the error
            view.setError(null);
        } else if (view.getText().toString().isEmpty()){
            // show the error
            view.setError(view.getContext().getString(R.string.invalid_field));
        }
    }

    @BindingAdapter("profileImage")
    public static void profileImage(ImageView view, String imageUrl) {

        if (imageUrl != null) {
            if (!imageUrl.isEmpty()) {
                // load profile image with Glide
                Glide.with(view.getContext())
                        .load(imageUrl)
                        .into(view);
            }
            
        } else {
            Glide.with(view.getContext())
                    .load(R.drawable.tictactoe)
                    .into(view);
        }
    }


    @BindingAdapter("visible")
    public static void setVisibility(View view, Boolean value) {
          view.setVisibility(value? View.VISIBLE :View.GONE );
    }


    @BindingAdapter("status")
    public static void setAlertDialogStatus(TextView textView, OnlineUsersViewModel.AlertDialogStatus status) {

        if (status == OnlineUsersViewModel.AlertDialogStatus.CONNECTING) {
            textView.setText(PLEASE_WAIT);
        } else if (status == OnlineUsersViewModel.AlertDialogStatus.CONNECTED ) {
            textView.setText(CONNECTED);
        }
    }
}
