package com.example.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NotificationActivity extends AppCompatActivity {
    EditText ed1,ed2,ed3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ed1=(EditText) findViewById(R.id.title);
        ed2=(EditText) findViewById(R.id.notification);
       // ed3=(EditText)findViewById(R.id.editText3);
        Button b1=(Button)findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tittle=ed1.getText().toString();
                String subject=ed2.getText().toString();
                String body=ed3.getText().toString();

                NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify=new Notification.Builder
                        (getApplicationContext()).setContentTitle(tittle).setContentText(body)
                        .setContentTitle(subject).setSmallIcon(R.mipmap.ic_launcher)
                        .setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_SOUND).build();

                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.notify(0, notify);
               // Toast.makeText(NotificationActivity.this, "Send Notification", Toast.LENGTH_SHORT).show();
            }
        });

//
    }
}