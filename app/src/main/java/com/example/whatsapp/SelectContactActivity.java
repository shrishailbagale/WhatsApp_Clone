package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.example.whatsapp.Adapters.ContactAdapter;

import java.util.ArrayList;

public class SelectContactActivity extends AppCompatActivity {

    TextView s_phonebook;
    ArrayList<String> arrayList;

   @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        s_phonebook = (TextView) findViewById(R.id.phonebook);
        setTitle("Contact Details");

        // to initilize the memory to arraylist

        arrayList = new ArrayList<String>();
        //give runtime permission to ARRAYLIST
        // thissploblem comes in marshmallow or greater version
        // this problem only for dangerous  permission like phonebook, sms, camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                (Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            // now request the permission
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 1);

        }
        else{
            //for lower than marsmallow version
            //to get the phomnebook
            getcontact();
        }
    }

    private void getcontact() {
        //to pass all phonebook
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        //to fetch all the contact from cursor
        while (cursor.moveToNext()){
            //pass the data into string from cursor
            @SuppressLint("Range") String name= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));



            // now ad the data into arraylist
            arrayList.add((name) + "\n"+ mobile+"\n\n");
            //to attach the arraylist into texview
            s_phonebook.setText(arrayList.toString());
        }
    }
    // to get the output of runtime permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //now permossion is granted call funtion again
                getcontact();
            }
        }
    }
}