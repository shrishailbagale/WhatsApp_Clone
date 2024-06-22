//ChatsFragment .java
package com.example.whatsapp.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.whatsapp.Adapters.UsersAdapter;

import com.example.whatsapp.Models.Users;

import com.example.whatsapp.R;
import com.example.whatsapp.databinding.FragmentChatsBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    ArrayList<Users> list= new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private AdView adView1213;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);


        database= FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //ads
        adView1213 = binding.adView.findViewById(R.id.adView);
        @SuppressLint("VisibleForTests") AdRequest adRequest = new AdRequest.Builder().build();
        adView1213.loadAd(adRequest);

        UsersAdapter adapter = new UsersAdapter(list, getContext());
        binding.chatRecyclarView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclarView.setLayoutManager(layoutManager);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());

                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return binding.getRoot();
    }
    @Override
    public void onDestroy() {
        adView1213.destroy();
        super.onDestroy();
    }
}
