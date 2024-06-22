package com.example.whatsapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapters.StatusAdapter;
import com.example.whatsapp.Models.Status;
import com.example.whatsapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatusFragment extends Fragment {

    private static final int PICK_MEDIA_REQUEST = 1;
    private RecyclerView statusRecyclerView;
    private StatusAdapter statusAdapter;
    private DatabaseReference statusDatabase;
    private StorageReference storageReference;
    private ImageButton editStatusButton, cameraStatusButton;
    private AdView adView1212;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        statusRecyclerView = view.findViewById(R.id.status_list);
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statusAdapter = new StatusAdapter();
        statusRecyclerView.setAdapter(statusAdapter);

        editStatusButton = view.findViewById(R.id.edit_status);
        cameraStatusButton = view.findViewById(R.id.camera_status);

        statusDatabase = FirebaseDatabase.getInstance().getReference("statuses");
        storageReference = FirebaseStorage.getInstance().getReference("statuses");

        loadStatuses();

        //ads
        adView1212 = view.findViewById(R.id.adView);
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        adView1212.loadAd(adRequest);

        editStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pickMedia();
            }
        });

        cameraStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia();
            }
        });

        return view;
    }

    private void loadStatuses() {
        statusDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Status> statusList = new ArrayList<>();
                for (DataSnapshot statusSnapshot : snapshot.getChildren()) {
                    Status status = statusSnapshot.getValue(Status.class);
                    statusList.add(status);
                }
                statusAdapter.setStatuses(statusList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void pickMedia() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri mediaUri = data.getData();
            if (mediaUri != null) {
                uploadMedia(mediaUri);
            }
        }
    }

    private void uploadMedia(Uri mediaUri) {
        final String mediaId = statusDatabase.push().getKey();
        final boolean isVideo = mediaUri.toString().contains("video");
        final StorageReference mediaRef = storageReference.child(mediaId);

        mediaRef.putFile(mediaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mediaRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userName = currentUser != null ? currentUser.getDisplayName() : "Unknown";
                                String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                                Status status = new Status(userName, currentTime, downloadUri.toString(), isVideo);
                                statusDatabase.child(mediaId).setValue(status);
                            } else {
                                Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Media upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        adView1212.destroy();
        super.onDestroy();
    }
}
