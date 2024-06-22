package com.example.whatsapp.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.Status;
import com.example.whatsapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private List<Status> statuses = new ArrayList<>();

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        Status status = statuses.get(position);
        holder.statusTextView.setText(status.getName());
        holder.timeTextView.setText(status.getTime());

        if (status.isVideo()) {
            // Handle video loading
            // You can use a library like ExoPlayer here to load and display video thumbnails
            // Placeholder code: holder.statusImageView.setImageResource(R.drawable.ic_video_placeholder);
        } else {
            Picasso.get().load(status.getMediaUrl()).into(holder.statusImageView);
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
        notifyDataSetChanged();
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {

        ImageView statusImageView;
        TextView statusTextView;
        TextView timeTextView;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            statusImageView = itemView.findViewById(R.id.status_image);
            statusTextView = itemView.findViewById(R.id.status_name);
            timeTextView = itemView.findViewById(R.id.status_time);
        }
    }
}
