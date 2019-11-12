package com.example.xvso;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private String videoPath = "https://www.youtube.com/watch?v=5n2aQ3UQu9Y&t=9s";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = findViewById(R.id.videoView);

        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setVideoPath(videoPath);

        MediaController mediaController = new MediaController(this);

        // attaches the media controller to the videoView
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);

        videoView.start();
    }
}
