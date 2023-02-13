package com.example.admin.Models;

public class UsersModel {

    String profilephoto, username, mail, mobile, password, userId, about;
    Double latitude,longitude;

    public UsersModel(String profilephoto, String mail, String password) {
        this.profilephoto = profilephoto;
        this.mail = mail;
        this.password = password;
    }

    public UsersModel(String profilephoto, String username, String mail, String mobile, String password, String userId, String about, Double latitude, Double longitude) {
        this.profilephoto = profilephoto;
        this.username = username;
        this.mail = mail;
        this.mobile = mobile;
        this.password = password;
        this.userId = userId;
        this.about = about;
        this.latitude = latitude;
        this.longitude = longitude;
    }



    public UsersModel() {
    }

//    public UsersModel(String profilephoto, String username, String mail, String mobile, String password, String userId, String about) {
//        this.profilephoto = profilephoto;
//        this.username = username;
//        this.mail = mail;
//        this.mobile = mobile;
//        this.password = password;
//        this.userId = userId;
//        this.about = about;
//
//    }

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}