package com.dynashwet.chatmate.Models;


public class profilegrid {

    private String id;
    private String img;
    private String text;
    private String posttype;


    public profilegrid()
    {

    }

    public profilegrid(String id, String img,String text,String posttype) {

        this.id = id;

        this.img = img;
        this.text = text;
        this.posttype = posttype;
    }

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}



