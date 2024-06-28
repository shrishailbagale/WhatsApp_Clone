package com.example.whatsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Models.Status;

public class StatusDetailActivity extends AppCompatActivity {

    private ImageView statusImageView;
    private VideoView statusVideoView;
    private TextView statusTextView;
    private ImageView userProfileImage;
    private TextView userNameTextView;
    private TextView statusTimeTextView;
    private EditText replyEditText;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        getSupportActionBar().hide();

        // Initialize views
        statusImageView = findViewById(R.id.status_image_view);
        statusVideoView = findViewById(R.id.status_video_view);
        statusTextView = findViewById(R.id.status_text_view);
        userProfileImage = findViewById(R.id.user_profile_image);
        userNameTextView = findViewById(R.id.user_name_text_view);
        statusTimeTextView = findViewById(R.id.status_time_text_view);
        replyEditText = findViewById(R.id.reply_edit_text);
        backButton = findViewById(R.id.back_button);

        // Get data from intent
        Intent intent = getIntent();
        Status status = (Status) intent.getSerializableExtra("status");

        if (status != null) {
            userNameTextView.setText(status.getUserName());
            statusTimeTextView.setText(getTimeAgo(status.getTimestamp()));

            if (status.getType().equals("image")) {
                statusImageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(status.getContentUrl()).into(statusImageView);
            } else if (status.getType().equals("video")) {
                statusVideoView.setVisibility(View.VISIBLE);
                statusVideoView.setVideoURI(Uri.parse(status.getContentUrl()));
                statusVideoView.start();
            } else if (status.getType().equals("text")) {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(status.getText());
            }

            // Load user profile image
            Glide.with(this).load(status.getUserProfileImageUrl()).into(userProfileImage);
        }

        backButton.setOnClickListener(v -> onBackPressed());

        // Handle reply action
        replyEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendReply();
                return true;
            }
            return false;
        });
    }

    private void sendReply() {
        String replyText = replyEditText.getText().toString().trim();
        if (!replyText.isEmpty()) {
            // Send reply logic
            Toast.makeText(this, "Reply sent: " + replyText, Toast.LENGTH_SHORT).show();
            replyEditText.setText("");
        }
    }

    private String getTimeAgo(long timestamp) {
        // Convert timestamp to "time ago" format
        return DateUtils.getRelativeTimeSpanString(timestamp).toString();
    }
}
