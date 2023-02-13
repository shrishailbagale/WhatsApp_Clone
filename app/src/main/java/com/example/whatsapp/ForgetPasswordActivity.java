package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    TextView signin;
    TextView signup;
    private EditText emailid;
    private Button btnforget;
    private String email;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().hide();

        signin = (TextView) findViewById(R.id.btnin);

        auth = FirebaseAuth.getInstance();

        emailid = (EditText) findViewById(R.id.txtEmail);
        btnforget = (Button) findViewById(R.id.forget);

        btnforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });





    }


    private void validateData(){
        email  = emailid.getText().toString();
        if (email.isEmpty()){
            emailid.setError("Required");
        }
        else {
            forgetPass();
        }
    }

    private  void forgetPass(){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Check your Email..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPasswordActivity.this, SignInActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(ForgetPasswordActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}