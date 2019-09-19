package com.dynashwet.chatmate.Models;

public class Notification {

    private  String title;
    private   String message;
    private  String image;
    private String fromID;
    String postID;
    String name;

    //For chat purposes
    public Notification(String title, String message, String image, String fromID, String name, String postID) {
        this.title = title;
        this.message = message;
        this.image = image;
        this.fromID = fromID;
        this.name = name;
        this.postID = postID;
    }



    public Notification(){

    }

    public String getName() {
        return name;
    }

    public String getPostID() {
        return postID;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
