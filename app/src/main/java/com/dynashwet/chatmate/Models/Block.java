package com.dynashwet.chatmate.Models;

public class Block {
    public String name;
    public String image;

    public Block(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
