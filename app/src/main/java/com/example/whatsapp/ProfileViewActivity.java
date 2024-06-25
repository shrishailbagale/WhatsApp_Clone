package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Models.Users;
import com.example.whatsapp.databinding.ActivityProfileViewBinding;
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

    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;

    private FirebaseAnalytics mFirebaseAnalytics;

    private String initialUsername;
    private String initialAbout;
    private String initialEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable back button
            actionBar.setTitle("Profile");
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        binding = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.saveProfile.setVisibility(View.GONE); // Hide the update button initially

        binding.saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String about = binding.about.getText().toString();
                final String name = binding.txtUsername.getText().toString();
                final String mobi = binding.txtMobile.getText().toString();
                final String email = binding.txtEmail.getText().toString();

                // Check if the email is already taken
                database.getReference().child("Users")
                        .orderByChild("mail")
                        .equalTo(email)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean emailExists = false;

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Users user = snapshot.getValue(Users.class);
                                    if (user != null && !snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                        // Email already exists for another user
                                        emailExists = true;
                                        break;
                                    }
                                }

                                if (emailExists) {
                                    Toast.makeText(ProfileViewActivity.this, "Email already exists, please choose another one", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Email does not exist, proceed with update
                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("username", name);
                                    obj.put("about", about);
                                    obj.put("mobile", mobi);
                                    obj.put("mail", email);

                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ProfileViewActivity.this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                                                    binding.saveProfile.setVisibility(View.GONE); // Hide the button after update
                                                    initialUsername = name; // Update initial values
                                                    initialAbout = about;
                                                    initialEmail = email;
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle onCancelled if needed
                            }
                        });
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

                        // Save the initial values
                        initialUsername = users.getUsername();
                        initialAbout = users.getAbout();
                        initialEmail = users.getMail();

                        addTextChangeListeners();
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
                intent.setType("image/*");
                startActivityForResult(intent, 1000);
            }
        });
    }

    private void addTextChangeListeners() {
        binding.txtUsername.addTextChangedListener(textWatcher);
        binding.about.addTextChangedListener(textWatcher);
        binding.txtEmail.addTextChangedListener(textWatcher);

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String currentUsername = binding.txtUsername.getText().toString();
            String currentAbout = binding.about.getText().toString();
            String currentEmail = binding.txtEmail.getText().toString();

            // Show the update button if there are changes
            if (!currentUsername.equals(initialUsername) || !currentAbout.equals(initialAbout) || !currentAbout.equals(initialEmail)) {
                binding.saveProfile.setVisibility(View.VISIBLE);
            } else {
                binding.saveProfile.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
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
