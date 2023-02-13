package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();

        v = (TextView) findViewById(R.id.version);
        String version ="Version " + BuildConfig.VERSION_NAME;
                //+ "\n" + "Version Code : " + BuildConfig.VERSION_CODE;
        v.setText(version);

    }
}