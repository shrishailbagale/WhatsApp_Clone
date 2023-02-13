package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.Models.Users;
import com.example.whatsapp.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity<idToken> extends AppCompatActivity {

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;

    EditText btnForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account!");



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.txtEmail.getText().toString().isEmpty()){
                    binding.txtEmail.setError("Enter your Email");
                    return;
                }
                if(binding.txtPassword.getText().toString() .isEmpty()){
                    binding.txtPassword.setError("Enter your Password");
                    return;
                }
                progressDialog.show();

                auth.signInWithEmailAndPassword(binding.txtEmail.getText().toString(),
                        binding.txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            progressDialog.show();
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(SignInActivity.this, "Invalid email and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        binding.btnMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SendOTPActivity.class);
                startActivity(intent);

            }
        });


        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        binding.btnSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        if(auth.getCurrentUser()!= null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));

        }
    }
    int RC_SIGN_IN = 65;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            //case RC_SIGN_IN:
            try {
                GoogleSignInAccount account  = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:success" +account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

                // }
            } catch (ApiException e) {
                Log.w("TAG", "Google login Failed!",e);
                Toast.makeText(SignInActivity.this, "Google login Failed!", Toast.LENGTH_SHORT).show();
            }
            // break;
        }
    }

    private void firebaseAuthWithGoogle(String idToken){

        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(firebaseCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:");
                        FirebaseUser user = auth.getCurrentUser();

                        Users users = new Users();
                        users.setUserId(user.getUid());
                        users.setUsername(user.getDisplayName());
                        users.setMobile(user.getPhoneNumber());
                        users.setProfilephoto(user.getPhotoUrl().toString());

                        database.getReference().child("Users").child(user.getUid()).setValue(users);

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:", task.getException());
                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Snackbar.make(binding.getRoot(), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}