//ChatAdapter.java

package com.example.whatsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends  RecyclerView.Adapter {

    String recieverId;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    ArrayList<MessageModel> messageModels;
    Context context;

    int SENDER_VIEW_TYPE = 1;
    int RECIEVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recieverId) {

        this.messageModels = messageModels;
        this.context = context;
        this.recieverId =recieverId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);

            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECIEVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete message");
                builder.setPositiveButton("Delete for me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getCurrentUser().getUid() + recieverId;
                        database.getReference().child("chats").child(senderRoom).removeValue().isSuccessful();
                        // .child(messageModel.getMessage()).setValue(equals(""));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

                return true;
            }
        });
        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());

            Date date = new Date(messageModel.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            String stringDate = simpleDateFormat.format(date);

            ((SenderViewHolder)holder).senderTime.setText(stringDate.toString());


        }
        else{
            ((RecieverViewHolder)holder).recieverMsg.setText(messageModel.getMessage());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            Date date = new Date(messageModel.getTimestamp());
            String stringDate = simpleDateFormat.format(date);
            ((RecieverViewHolder)holder).recieverTime.setText(stringDate.toString());
        }
    }

    @Override
    public int getItemCount() {

        return messageModels.size();
    }

    public static class RecieverViewHolder extends RecyclerView.ViewHolder{

        TextView recieverMsg , recieverTime;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);

            recieverMsg = itemView.findViewById(R.id.recieverText);
            recieverTime = itemView.findViewById(R.id.recieverTime);
        }
    }


    public static class SenderViewHolder extends  RecyclerView.ViewHolder{

        TextView senderMsg , senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

//    private void deleteMessage(int position) {
//
//        String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Long msgtimestmp = messageModels.get(position).getTimestamp();
//        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("chats");
//        Query query = dbref.orderByChild("timestamp").equalTo(msgtimestmp);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    ds.getRef().removeValue();
//                    if (ds.child("chats").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        // any two of below can be used
//                     //   ds.getRef().removeValue();
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted");
//                        ds.getRef().updateChildren(hashMap);
//                        Toast.makeText(context,"Message Deleted.....",Toast.LENGTH_LONG).show();
//
//                    }
//                    else {
//                        Toast.makeText(context, "you can delete only your msg....", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}