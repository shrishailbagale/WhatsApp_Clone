package com.example.whatsapp.Models;

import android.net.Uri;
import android.widget.ImageView;

public class MessageModel {

    String uId, message,type;
    Long timestamp , image;

    public MessageModel(String type, Long timestamp, Long image) {
        this.type = type;
        this.timestamp = timestamp;
        this.image = image;
    }


    public MessageModel(String uId, String message, Long timestamp, String type) {
        this.uId = uId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;

    }

    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public MessageModel(){}

    public MessageModel(ImageView sent, Uri image_uri) {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getImage() {
        return image;
    }

    public void setImage(Long image) {
        this.image = image;
    }

    public void getImage(String message, String s) {
    }

    public void getuId(String sender, String s) {
    }
}