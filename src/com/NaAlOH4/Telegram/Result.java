package com.NaAlOH4.Telegram;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("ok")
    public boolean ok;
    @SerializedName("error_code")
    public String errorCode;
    @SerializedName("description")
    public String description;
    @SerializedName("result")
    public JsonElement result;
}
