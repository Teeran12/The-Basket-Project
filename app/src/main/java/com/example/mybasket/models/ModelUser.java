package com.example.mybasket.models;

public class ModelUser {
    String name, email, profileImage, phone, uid, online, typingTo;


    public ModelUser() {

    }

    public ModelUser(String name, String email, String profileImage, String phone, String uid, String online, String typingTo) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.phone = phone;
        this.uid = uid;
        this.online = online;
        this.typingTo = typingTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }
}
