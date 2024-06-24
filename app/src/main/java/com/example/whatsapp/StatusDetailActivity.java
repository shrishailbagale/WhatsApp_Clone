package com.example.whatsapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class StatusDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private VideoView videoView;
    private boolean isVideo;
    private String mediaUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        getSupportActionBar().hide();

        imageView = findViewById(R.id.status_image_view);
        videoView = findViewById(R.id.status_video_view);

        if (getIntent() != null) {
            mediaUrl = getIntent().getStringExtra("mediaUrl");
            isVideo = getIntent().getBooleanExtra("isVideo", false);
        }

        if (isVideo) {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(mediaUrl));
            videoView.start();
        } else {
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mediaUrl).into(imageView);
        }
    }
}
