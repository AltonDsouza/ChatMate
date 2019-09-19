package com.dynashwet.chatmate.Models;


public class Singlefriend {

    private String name;
    private String img;


    public Singlefriend()
    {

    }

    public Singlefriend(String name, String img) {

        this.name = name;

        this.img = img;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}


