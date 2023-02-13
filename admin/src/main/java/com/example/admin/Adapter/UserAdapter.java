package com.example.admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.Models.UsersModel;
import com.example.admin.R;
import com.example.admin.descfragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirebaseRecyclerAdapter<UsersModel, UserAdapter.myviewholder> {


    public UserAdapter(@NonNull FirebaseRecyclerOptions<UsersModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull final UsersModel model) {

      holder.nametext.setText(model.getUsername());
      holder.mobi.setText(model.getMobile());
      Picasso.get().load(model.getProfilephoto()).placeholder(R.drawable.profile).into(holder.img1);
     // Glide.with(holder.img1.getContext()).load(model.getProfilephoto()).placeholder(R.drawable.profile).into(holder.img1);

              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                      AppCompatActivity activity=(AppCompatActivity)view.getContext();
                      activity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper,new descfragment( model.getUsername(),model.getMobile(),model.getMail(),model.getProfilephoto(),model.getAbout(),model.getPassword(),model.getLatitude(),model.getLongitude())).addToBackStack(null).commit();
                  }
              });
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_user,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img1;
        TextView nametext,mobi;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            img1=itemView.findViewById(R.id.profilephoto);
            nametext=itemView.findViewById(R.id.username);
            mobi=itemView.findViewById(R.id.mobile);
        }
    }

}
