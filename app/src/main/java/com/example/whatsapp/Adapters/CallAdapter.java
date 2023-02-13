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

import com.example.whatsapp.CallActivity;
import com.example.whatsapp.ChatDetailActivity;
import com.example.whatsapp.Models.Users;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder>{

    ArrayList<Users> list;
    Context context;

    public CallAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_call_item , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = list.get(position);
        Picasso.get() .load(users.getProfilephoto()).placeholder(R.drawable.profile).into(holder.image);
        holder.username.setText(users.getUsername());
        holder.number.setText(users.getMobile());

        FirebaseDatabase.getInstance().getReference()
                .child("chats").child(FirebaseAuth.getInstance()
                        .getUid() + users.getUserId()).orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                //holder.lastMessage.setText(snapshot1.child("mobile").getValue(String.class).toString());
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
                Intent intent = new Intent(context , CallActivity.class);
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("username", users.getUsername());
                intent.putExtra("mobile", users.getMobile());
                intent.putExtra("profilephoto" , users.getProfilephoto());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, call;
        TextView username, number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profilephoto);
            username = itemView.findViewById(R.id.username);
            number = itemView.findViewById(R.id.number);
            call = itemView.findViewById(R.id.btncall);

        }
    }
}