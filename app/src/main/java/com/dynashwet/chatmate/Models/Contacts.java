package com.dynashwet.chatmate.Models;

public class Contacts {

    private String name;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String contact) {
        this.image = contact;
    }

    public Contacts(String name, String image) {
        this.name = name;
        this.image = image;
    }

}
