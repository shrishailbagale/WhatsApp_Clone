package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Models.Status;
import com.example.whatsapp.R;
import com.example.whatsapp.StatusDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private List<Status> statusList;
    private Context context;
    private static final long EXPIRY_TIME_MILLIS = 24 * 60 * 60 * 1000; // 24 hours

    public StatusAdapter(Context context, List<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Status status = statusList.get(position);
        long currentTime = System.currentTimeMillis();
        long statusTime = status.getTimestamp();
        long elapsedTime = currentTime - statusTime;

        // Check if the status has expired
        if (elapsedTime > EXPIRY_TIME_MILLIS) {
            // Remove expired status from Firebase
            deleteStatusFromFirebase(status);
        } else {
            Date date = new Date(status.getTimestamp());

            // Set data to views (e.g., profile image, timestamp, etc.)
            holder.userNameTextView.setText(status.getUserName());
            holder.statusTimeTextView.setText(getTimeAgo(status.getTimestamp()));
            Glide.with(context).load(status.getContentUrl()).into(holder.userProfileImage);

            // Get current user ID
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Check if the status belongs to the current user
            if (status.getStatusId().equals(currentUserId)) {
                holder.deleteStatus.setVisibility(View.VISIBLE);
                holder.deleteStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show confirmation dialog
                        showDeleteConfirmationDialog(status);
                    }
                });
            } else {
                holder.deleteStatus.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, StatusDetailActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView userNameTextView, statusTimeTextView;
        ImageView deleteStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.status_image);
            userNameTextView = itemView.findViewById(R.id.status_name);
            statusTimeTextView = itemView.findViewById(R.id.status_time);
            deleteStatus = itemView.findViewById(R.id.delete_status);
        }
    }

    private String getTimeAgo(long timestamp) {
        // Convert timestamp to "time ago" format
        return DateUtils.getRelativeTimeSpanString(timestamp).toString();
    }

    private void deleteStatusFromFirebase(Status status) {
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("statuses");
        String statusKey = status.getStatusKey(); // Adjust this according to your Status model
        statusRef.child(statusKey).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Status deleted successfully
                        Toast.makeText(context, "Status deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(context, "Failed to delete status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmationDialog(Status status) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Status")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete this status?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteStatusFromFirebase(status);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
