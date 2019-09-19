package com.dynashwet.chatmate.Models;

import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;

public class Public {
    String name;
    String isLike;
    String caption;
    String Post_ID;
    String post_type;
    String upload_time;

    String text;
    String isShare;
    String pro_image;
    String  image;
    String uploadBy;
    String isBlock;
    String likes;
    String isFriend;
    String video;
    String commCount;

    public String getCommCount() {
        return commCount;
    }

    public Public() {
    }

    public String getIsShare() {
        return isShare;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsShare(String isShare) {
        this.isFriend = isShare;
    }

    public String getIsBlock() {
        return isBlock;
    }

    public String getUploadBy() {
        return uploadBy;
    }


    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getPost_type() {
        return post_type;
    }

    public String getPost_ID() {
        return Post_ID;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public void setPost_ID(String post_ID) {
        Post_ID = post_ID;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getLikes() {
        return likes;
    }

    //If the post is an image
    public Public(String name, String caption,String profile_image, String image, String post_type,
                  String post_id, String upload_time, String isLike, String uploadBy, String likes, String isFriend,String commCount, String isShare) {
        this.name = name;
        this.image = image;
        this.pro_image = profile_image;
        this.post_type = post_type;
        this.Post_ID = post_id;
        this.upload_time = upload_time;
        this.isLike = isLike;
        this.caption = caption;
        this.uploadBy = uploadBy;
        this.likes = likes;
        this.isFriend = isFriend;
        this.commCount = commCount;
        this.isShare = isShare;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPro_image() {
        return pro_image;
    }

    public void setPro_image(String pro_image) {
        this.pro_image = pro_image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
