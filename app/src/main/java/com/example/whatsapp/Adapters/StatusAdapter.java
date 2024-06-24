package com.example.whatsapp.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.Status;
import com.example.whatsapp.R;
import com.example.whatsapp.StatusDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private List<Status> statuses = new ArrayList<>();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference statusDatabase = FirebaseDatabase.getInstance().getReference("statuses");

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Status status = statuses.get(position);
        holder.statusTextView.setText(status.getName());
        holder.timeTextView.setText(status.getTime());

        if (status.isVideo()) {
            // Handle video loading
            // Placeholder code: holder.statusImageView.setImageResource(R.drawable.ic_video_placeholder);
        } else {
            Picasso.get().load(status.getMediaUrl()).into(holder.statusImageView);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StatusDetailActivity.class);
            intent.putExtra("mediaUrl", status.getMediaUrl());
            intent.putExtra("isVideo", status.isVideo());
            v.getContext().startActivity(intent);
        });

        if (auth.getCurrentUser() != null && auth.getCurrentUser().getUid().equals(status.getUserId())) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(holder.itemView.getContext(), status, position);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
        notifyDataSetChanged();
    }

    private void showDeleteConfirmationDialog(Context context, Status status, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this status?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Show loading indicator
                    ProgressBar progressBar = new ProgressBar(context);
                    AlertDialog loadingDialog = new AlertDialog.Builder(context)
                            .setView(progressBar)
                            .setCancelable(false)
                            .setTitle("Status Deleting")
                            .setMessage("Please Wait...")
                            .create();
                    loadingDialog.show();

                    // Perform the delete operation
                    deleteStatus(status, position, loadingDialog);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteStatus(Status status, int position, AlertDialog loadingDialog) {
        // Remove status from database
        statusDatabase.child(status.getUserId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Optionally, remove the media from Firebase Storage
                FirebaseStorage.getInstance().getReferenceFromUrl(status.getMediaUrl()).delete().addOnCompleteListener(storageTask -> {
                    loadingDialog.dismiss();
                    if (storageTask.isSuccessful()) {
                        // Remove item from the list and notify adapter
                        statuses.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(loadingDialog.getContext(), "Status deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(loadingDialog.getContext(), "Failed to delete media", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loadingDialog.dismiss();
                Toast.makeText(loadingDialog.getContext(), "Failed to delete status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {

        ImageView statusImageView;
        TextView statusTextView;
        TextView timeTextView;
        ImageView deleteButton;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            statusImageView = itemView.findViewById(R.id.status_image);
            statusTextView = itemView.findViewById(R.id.status_name);
            timeTextView = itemView.findViewById(R.id.status_time);
            deleteButton = itemView.findViewById(R.id.delete_status);
        }
    }
}