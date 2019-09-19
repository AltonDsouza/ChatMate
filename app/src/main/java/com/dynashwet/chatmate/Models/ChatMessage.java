package com.dynashwet.chatmate.Models;

import java.util.Date;

public class ChatMessage {

    public static final int SENT=1;
    public static final int RECEIVED=2;

    public int type;
    private String messageText;
    private String messageUser;
    private String UserImage;
    private String messageTime;


    public ChatMessage(String messageText, String messageUser, String userImage, String messageTime,int type) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        UserImage = userImage;
        this.type = type;
        this.messageTime = messageTime;
    }

    public ChatMessage(String messageText, String messageTime,int type) {
        this.type = type;
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public int getType() {
        return type;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
