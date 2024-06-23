package com.example.whatsapp.Models;

public class Status {
    private String id;  // Unique identifier for the status
    private String userId;
    private String username;
    private String time;
    private String mediaUrl;
    private boolean isVideo;

    public Status() {
        // Default constructor required for calls to DataSnapshot.getValue(Status.class)
    }

    public Status(String id, String userId, String username, String time, String mediaUrl, boolean isVideo) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.time = time;
        this.mediaUrl = mediaUrl;
        this.isVideo = isVideo;
    }

    public Status(String userId, String username, String time, String mediaUrl, boolean isVideo) {
        this.userId = userId;
        this.username = username;
        this.time = time;
        this.mediaUrl = mediaUrl;
        this.isVideo = isVideo;
    }

    // Getters and setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return username; }
    public void setName(String name) { this.username = name; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public boolean isVideo() { return isVideo; }
    public void setVideo(boolean video) { isVideo = video; }
}
