package com.example.whatsapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapters.StatusAdapter;
import com.example.whatsapp.Models.Status;
import com.example.whatsapp.R;
import com.example.whatsapp.UploadStatusActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {

    private static final int PICK_MEDIA_REQUEST = 1;
    private RecyclerView statusRecyclerView;
    private ImageButton fabUploadStatus;
    private StatusAdapter statusAdapter;
    private List<Status> statusList;
    private ImageView deleteStatus, cameraButton, editButton;
    private AdView adView1212;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        

        statusRecyclerView = view.findViewById(R.id.status_recycler_view);
        fabUploadStatus = view.findViewById(R.id.fab_upload_status);
        deleteStatus = view.findViewById(R.id.delete_status);
        cameraButton = view.findViewById(R.id.fab_upload_status);
        editButton = view.findViewById(R.id.edit_status);

        statusList = new ArrayList<>();
        statusAdapter = new StatusAdapter(getContext(), statusList);
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statusRecyclerView.setAdapter(statusAdapter);

        //ads
        adView1212 = view.findViewById(R.id.adView);
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        adView1212.loadAd(adRequest);

        // Fetch statuses from Firebase
        fetchStatuses();

        fabUploadStatus.setOnClickListener(v -> {
            // Open status upload activity
            startActivity(new Intent(getContext(), UploadStatusActivity.class));
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia();
            }
        });

        return view;
    }

    private void pickMedia() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }

    private void fetchStatuses() {
        FirebaseDatabase.getInstance().getReference("statuses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        statusList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Status status = dataSnapshot.getValue(Status.class);
                            if (status != null) {
                                statusList.add(status);
                            }
                        }
                        statusAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private String getTimeAgo(long timestamp) {
        // Convert timestamp to "time ago" format
        return DateUtils.getRelativeTimeSpanString(timestamp).toString();
    }

}
