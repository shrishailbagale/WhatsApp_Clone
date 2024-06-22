package com.example.whatsapp.Models;

public class Status {
    private String name;
    private String time;
    private String mediaUrl;
    private boolean isVideo;

    public Status() {
        // Default constructor required for calls to DataSnapshot.getValue(Status.class)
    }

    public Status(String name, String time, String mediaUrl, boolean isVideo) {
        this.name = name;
        this.time = time;
        this.mediaUrl = mediaUrl;
        this.isVideo = isVideo;
    }

    // Getters and setters for all fields


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
