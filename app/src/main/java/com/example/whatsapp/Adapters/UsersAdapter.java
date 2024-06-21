package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ChatDetailActivity;
import com.example.whatsapp.Models.Users;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    ArrayList<Users> list ;
    Context context;
    private SimpleDateFormat simpleDateFormat;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = list.get(position);
        Picasso.get() .load(users.getProfilephoto()).placeholder(R.drawable.profile).into(holder.image);
        holder.username.setText(users.getUsername());

        FirebaseDatabase.getInstance().getReference()
                .child("chats").child(FirebaseAuth.getInstance()
                        .getUid() + users.getUserId()).orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        simpleDateFormat = new SimpleDateFormat("HH:mm a");
                        if(snapshot.hasChildren()){
                            DataSnapshot lastMessageSnapshot = null;
                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                lastMessageSnapshot = snapshot1;
                            }
                            if (lastMessageSnapshot != null) {
                                String lastMessage = lastMessageSnapshot.child("message").getValue(String.class);
                                Long timestamp = lastMessageSnapshot.child("timestamp").getValue(Long.class);
                                if (lastMessage != null) {
                                    holder.lastMessage.setText(lastMessage);
                                }
                                if (timestamp != null) {
                                    String lastMessageTime = convertTimestampToTime(timestamp);
                                    holder.lastTime.setText(lastMessageTime);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , ChatDetailActivity.class);

                    intent.putExtra("userId", users.getUserId());
                    intent.putExtra("profilephoto" , users.getProfilephoto());
                    intent.putExtra("username" , users.getUsername());


                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView username, lastMessage, lastTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profilephoto);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastTime = itemView.findViewById(R.id.lastTime);
        }
    }
    // Convert timestamp to time
    private String convertTimestampToTime(Long timestamp) {
        return simpleDateFormat.format(new Date(timestamp));
    }
}