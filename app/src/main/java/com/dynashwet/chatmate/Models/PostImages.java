package com.dynashwet.chatmate.Models;

public class PostImages {

    public String image;
    public String postID;

    public PostImages(String image, String postID) {
        this.image = image;
        this.postID = postID;
    }

    public String getPostID() {
        return postID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
