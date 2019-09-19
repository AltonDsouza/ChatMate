package com.dynashwet.chatmate;

public class ChatModel {

    private String user_id;
    private String time;
    private String message;
    private String mid;


    public ChatModel()
    {

    }

    public ChatModel(String user_id, String time, String message, String mid) {
        this.user_id = user_id;
        this.time = time;
        this.message = message;
        this.mid = mid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

