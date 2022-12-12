package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.whatsapp.Adapters.ContactAdapter;
import com.example.whatsapp.Models.ContactModel;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //check permisioon
        checkPermission();

    }

    private void checkPermission(){
        //check condition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M            && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                //When Permission is not granted
                //Request permission
                ActivityCompat.requestPermissions(ContactListActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, 100);
            }else{
                //When permission is granted
                //not requested permission
                getContactList();
            }
        }




    private void getContactList() {
        //Initialize uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sortb by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+"ASC";

        //Initialize cursor
        Cursor cursor = getContentResolver().query(uri, null, null, null,null);

        //check condition
        if (cursor.getCount()> 0){
            //when count is greter than 0
            //Use while loop
            while (cursor.moveToNext()){
                //cURSOR MOVE TO NEXT
                //gET CONTACT ID
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                //Get contact name
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //Initialize phone uri
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                //Initialize selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";

                //Initializ phone cursor
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection,new String[]{id},null);

                //Check condition

                if (phoneCursor.moveToNext()){
                    //When phone cursor name to next
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Initialize contact model
                    ContactModel model = new ContactModel();
                    //Set name
                    model.setName(name);
                    //set number
                    model.setNumber(number);
                    //Add  model in array list
                    arrayList.add(model);
                    //close phone cursor
                    phoneCursor.close();

                }
                ///close cursor
                cursor.close();

            }
            //Set layout mananger
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //Initialize adapter
            adapter = new ContactAdapter(this, arrayList);
            //set adapter
             recyclerView.setAdapter(adapter);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Check condition
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //when permission is granted
            //Call method
            getContactList();
        }else{
            //When permission is denied
            //Display toast
            Toast.makeText(ContactListActivity.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            //Call check permission method
            checkPermission();
        }
    }
}