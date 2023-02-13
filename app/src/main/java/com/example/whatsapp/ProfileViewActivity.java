package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Models.Users;
import com.example.whatsapp.databinding.ActivityProfileViewBinding;
import com.example.whatsapp.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileViewActivity extends AppCompatActivity {

    ActivityProfileViewBinding binding;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        setTitle("Profile");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        binding = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String about = binding.about.getText().toString();
                String name = binding.txtUsername.getText().toString();
                String mobi = binding.txtMobile.getText().toString();
                String email = binding.txtEmail.getText().toString();

                HashMap<String , Object> obj = new HashMap<>();
                obj.put("username", name );
                obj.put("about", about);
                obj.put("mobile", mobi);
                obj.put("mail", email);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(ProfileViewActivity.this, "Profile Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Users users = snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfilephoto()).placeholder(R.drawable.profile)
                                .into(binding.profileImage);
                        binding.about.setText(users.getAbout());
                        binding.txtUsername.setText(users.getUsername());
                        binding.txtMobile.setText(users.getMobile());
                        binding.txtEmail.setText(users.getMail());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        binding.changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");  // */*
                startActivityForResult(intent, 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null){
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);

            progressDialog = new ProgressDialog(ProfileViewActivity.this);
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Profile photo updating...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference reference = storage.getReference().child("profile_pictures")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @SuppressLint("WrongViewCast")
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();

                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilephoto").setValue(uri.toString());
                            Toast.makeText(ProfileViewActivity.this, "Profile photo Uploaded!", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            });
        }
    }
}