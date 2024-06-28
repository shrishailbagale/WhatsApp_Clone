package com.example.whatsapp.Models;

import java.io.Serializable;

public class Status implements Serializable {
    private String statusId;
    private String userName;
    private String userProfileImageUrl;
    private String contentUrl;
    private String text;
    private String type; // "image", "video", or "text"
    private long timestamp;

    public Status() {
        // Default constructor required for calls to DataSnapshot.getValue(Status.class)
    }

    public Status(String statusId, String userName, String userProfileImageUrl, String contentUrl, String text, String type, long timestamp) {
        this.statusId = statusId;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.contentUrl = contentUrl;
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Status(String userName, String userProfileImageUrl, String contentUrl, String text, String type, long timestamp) {
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.contentUrl = contentUrl;
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters and setters
    // ...


    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusKey() {
        return "";
    }
}
