package com.dynashwet.chatmate.Models;

import com.android.volley.toolbox.StringRequest;

public class Comments {
    private String comment;
    private String user_id;
    private String image;
    private String username;

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }

    public Comments(String comment, String username, String image) {
        this.comment = comment;
        this.username = username;
        this.image = image;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public String getUser_id() {
        return user_id;
    }
}
