package com.example.mybasket.models;

public class ModelChatView {
    String uid, name, profileImage, message, timestamp;// we will need this id to get chat list, sender/receiver uid

    public ModelChatView() {
    }

    public ModelChatView(String uid, String name, String profileImage, String message, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
