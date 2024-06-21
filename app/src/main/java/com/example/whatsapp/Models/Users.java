package com.example.whatsapp.Models;

public class Users {

    String profilephoto, username, mail, mobile, password, userId, about;
    Long timestamp;

    public Users(String profilephoto, String username, String mail,String mobile,String password, String lastMessage) {
        this.profilephoto = profilephoto;
        this.username = username;
        this.mail = mail;
        this.mobile = mobile;
        this.password = password;
        this.timestamp = timestamp;
    }

    public Users(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Users() {

    }

    //Signup constructor
    public Users(String username, String mail, String about, String mobile,String password) {
        this.username = username;
        this.mail = mail;
        this.mobile = mobile;
        this.about = about;
        this.password = password;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {  return mobile; }

    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }



}