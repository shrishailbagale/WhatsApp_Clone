package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.whatsapp.databinding.ActivitySignUpBinding;

public class ForgetPasswordActivity extends AppCompatActivity {



    ActivitySignUpBinding binding;
    TextView signin;
    TextView signup;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        signin = (TextView) findViewById(R.id.btnin);
        signup = (TextView) findViewById(R.id.btnup);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
                startActivity(intent);

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPasswordActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });
    }
}