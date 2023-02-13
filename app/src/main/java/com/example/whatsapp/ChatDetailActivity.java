package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.example.whatsapp.Adapters.ChatAdapter;
import com.example.whatsapp.Models.MessageModel;

import com.example.whatsapp.databinding.ActivityChatDetailBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatDetailActivity extends AppCompatActivity  {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    EditText etMess;
    ImageView link, img ,sent ;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;

    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 2000;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri image_uri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        img = (ImageView) findViewById(R.id.btnCamera);
        link = (ImageView) findViewById(R.id.btnlink);
        sent = (ImageView) findViewById(R.id.send);



        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        cameraPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };


        etMess = (EditText) findViewById(R.id.etMessage);

        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image*/*");  // */*
//                startActivityForResult(intent, 33);
                showImageImportDialog();
            }

        });


        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String recieverId = getIntent().getStringExtra("userId");
        String userName  = getIntent().getStringExtra("username");
        String profileimage = getIntent().getStringExtra("profilephoto");

        binding.username.setText(userName);
        Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(binding.profilephoto);

        // Back Button start
        binding.btnbackchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });
        //back Button end

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, ChatDetailActivity.this,recieverId);
        binding.chatRecyclarView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclarView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + recieverId;
        final String recieverRoom = recieverId + senderId;

        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });






        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message =  binding.etMessage.getText().toString();
                final  MessageModel model = new MessageModel(senderId, message);
                model.setTimestamp(new Date().getTime());

                if (message.isEmpty()){
                    Toast.makeText(ChatDetailActivity.this, "Cant send empty message", Toast.LENGTH_SHORT).show();
                }else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(ChatDetailActivity.this, R.raw.sent);
                    mediaPlayer.start();
                    binding.etMessage.setText("");
                   // MessageModel model1 = new MessageModel();
                    model.setType("text");

                    database.getReference().child("chats").child(senderRoom).push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.getReference().child("chats").child(recieverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            });
                }
            }
        });

        binding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this , UserDetailsActivity.class);
                startActivity(intent);
            }
        });
    }


    public void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.chat_menu, popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  R.id.clearchat:
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String senderRoom = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database.getReference().child("chats").child(senderRoom).removeValue().isSuccessful();
                break;

            case R.id.wallpaper:
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (resultCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                sentImageMessage();
                Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show();

            }
            if (resultCode == IMAGE_PICK_CAMERA_CODE){

                sentImageMessage();


            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length> 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }else{
                        Toast.makeText(this, "Camera Permission required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 ){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    }else {
                        Toast.makeText(this, "Storage Permission required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    private void showImageImportDialog() {
        String[] option = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this);
        builder.setTitle("Pick Image").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    //camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickCamera();
                    }
                }
                else{
                    //gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickGallery();
                    }
                }
            }
        }).show();
    }

    private void pickGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");  // */*
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    private void pickCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "ImageTitle");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "ImageDescription");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(ChatDetailActivity.this,storagePermission, STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(ChatDetailActivity.this,cameraPermission, CAMERA_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(ChatDetailActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(ChatDetailActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void sentImageMessage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Image sending...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //file name and path firebase storage
        String fileNamePath = "chat_images/"+ ""+System.currentTimeMillis();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNamePath);
        storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Image upload, get url
                Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTask.isSuccessful());
                Uri p_downloadUri = p_uriTask.getResult();


                if (p_uriTask.isSuccessful()){
                    //Image url recived , save in db
                    MessageModel model = new MessageModel();

                    //timestamp
                    String timestamp = ""+System.currentTimeMillis();


                    //setup message data
                    HashMap<String , Object> hashMap = new HashMap<>();
                    hashMap.put("uId",""+ auth.getUid());
                    hashMap.put("message",""+p_downloadUri);
                    hashMap.put("timestamp",""+timestamp);
                    hashMap.put("type",""+"image");//text/image/file

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");

                    databaseReference.child("chats").child("message").child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            binding.etMessage.setText("");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChatDetailActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed uploading image
                Toast.makeText(ChatDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}