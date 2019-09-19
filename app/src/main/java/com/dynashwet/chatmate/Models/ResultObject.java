package com.dynashwet.chatmate.Models;

import com.google.gson.annotations.SerializedName;

public class ResultObject {
    @SerializedName("Success")
    public boolean success;
    @SerializedName("Msg")
   public String message;
    @SerializedName("Reason")
    public String reason;
    @SerializedName("Type")
    public String Type;

    public String getType() {
        return Type;
    }

    public String getReason() {
        return reason;
    }
    public String getMessage() {
        return message;
    }
   public boolean getSuccess() {
        return success;
    }
}
