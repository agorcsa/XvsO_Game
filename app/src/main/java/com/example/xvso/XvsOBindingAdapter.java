package com.example.xvso;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class XvsOBindingAdapter {

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
        if (isGameInProgress == true) {
            imageView.setClickable(true);
        } else {
            imageView.setClickable(false);
        }
    }

    @BindingAdapter({"name", "firstName"})
    public static void displayUserName(TextView textView, String name, String firstName) {
        if (TextUtils.isEmpty(firstName))  {
            // display name
            textView.setText(name);
        } else {
            // display firstName
            textView.setText(firstName);
        }
    }
}
