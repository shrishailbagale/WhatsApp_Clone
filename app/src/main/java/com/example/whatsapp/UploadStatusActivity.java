package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Models.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadStatusActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_SELECT_VIDEO = 2;

    private EditText statusTextInput;
    private ImageView statusImagePreview;
    private VideoView statusVideoPreview;
    private Button selectImageButton;
    private Button selectVideoButton;
    private Button uploadStatusButton;
    private ProgressBar uploadProgressBar;
    private TextView uploadProgressText;
    private Button cancelUploadButton;

    private Uri selectedMediaUri;
    private String selectedMediaType;
    private String userId;
    private String userName;
    private String userProfileImageUrl;
    private ImageButton backButton;
    private UploadTask uploadTask; // Reference to the upload task

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_status);
        getSupportActionBar().hide();
        setTitle("Upload Status");

        statusTextInput = findViewById(R.id.status_text_input);
        statusImagePreview = findViewById(R.id.status_image_preview);
        statusVideoPreview = findViewById(R.id.status_video_preview);
        selectImageButton = findViewById(R.id.select_image_button);
        selectVideoButton = findViewById(R.id.select_video_button);
        uploadStatusButton = findViewById(R.id.upload_status_button);
        uploadProgressBar = findViewById(R.id.upload_progress_bar);
        uploadProgressText = findViewById(R.id.upload_progress_text);
        cancelUploadButton = findViewById(R.id.cancel_upload_button);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> onBackPressed());

        // Get the current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user data
        fetchUserData();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMedia("image/*", REQUEST_CODE_SELECT_IMAGE);
            }
        });

        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMedia("video/*", REQUEST_CODE_SELECT_VIDEO);
            }
        });



        uploadStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStatus();
            }
        });

        cancelUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    uploadTask.cancel();
                    Toast.makeText(UploadStatusActivity.this, "Upload canceled", Toast.LENGTH_SHORT).show();
                    // Reload the activity
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    private void fetchUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userName = dataSnapshot.child("username").getValue(String.class);
                    userProfileImageUrl = dataSnapshot.child("profilephoto").getValue(String.class);
                } else {
                    Toast.makeText(UploadStatusActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadStatusActivity.this, "Failed to read user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectMedia(String type, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedMediaUri = data.getData();
            statusTextInput.setVisibility(View.GONE);
            uploadStatusButton.setVisibility(View.GONE);

            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                selectedMediaType = "image";
                statusImagePreview.setVisibility(View.VISIBLE);
                statusVideoPreview.setVisibility(View.GONE);
                statusTextInput.setVisibility(View.VISIBLE);
                selectImageButton.setVisibility(View.GONE);
                selectVideoButton.setVisibility(View.GONE);
                uploadStatusButton.setVisibility(View.VISIBLE);
                Glide.with(this).load(selectedMediaUri).into(statusImagePreview);

            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                selectedMediaType = "video";
                statusImagePreview.setVisibility(View.GONE);
                statusVideoPreview.setVisibility(View.VISIBLE);
                statusTextInput.setVisibility(View.VISIBLE);
                statusTextInput.setVisibility(View.VISIBLE);
                statusVideoPreview.setVideoURI(selectedMediaUri);
                selectImageButton.setVisibility(View.GONE);
                selectVideoButton.setVisibility(View.GONE);
                uploadStatusButton.setVisibility(View.VISIBLE);
                statusVideoPreview.start();
            }
        }
    }

    private void uploadStatus() {
        if (selectedMediaUri == null && statusTextInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter text or select media", Toast.LENGTH_SHORT).show();
            return;
        }

        final DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("statuses").push();
        final String statusId = statusRef.getKey();
        final long timestamp = System.currentTimeMillis();

        if (selectedMediaUri != null) {

            final StorageReference mediaRef = FirebaseStorage.getInstance().getReference("status_media").child(statusId);
            uploadTask = (UploadTask) mediaRef.putFile(selectedMediaUri)
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        uploadProgressBar.setVisibility(View.VISIBLE);
                        uploadProgressText.setVisibility(View.VISIBLE);
                        uploadStatusButton.setVisibility(View.GONE);
                        cancelUploadButton.setVisibility(View.VISIBLE);
                        uploadProgressBar.setProgress((int) progress);
                        uploadProgressText.setText(String.format("%d%%", (int) progress));
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mediaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String mediaUrl = uri.toString();
                                    Status status = new Status(userId, userName, userProfileImageUrl, mediaUrl, statusTextInput.getText().toString().trim(), selectedMediaType, timestamp);
                                    statusRef.setValue(status);
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadStatusActivity.this, "Failed to upload media", Toast.LENGTH_SHORT).show();
                            uploadProgressBar.setVisibility(View.GONE);
                            uploadProgressText.setVisibility(View.GONE);
                            cancelUploadButton.setVisibility(View.GONE);
                        }
                    });
        } else {

            Status status = new Status(userName, userProfileImageUrl, null, statusTextInput.getText().toString().trim(), "text", timestamp);
            statusRef.setValue(status);
            finish();
        }
    }
}
