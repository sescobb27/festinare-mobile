package com.festinare.discount.models;

import com.google.gson.annotations.SerializedName;

public class Mobile {

    @SerializedName("_id")
    private String id;
    private String token; // GCM_ID
    private String platform = "android";

    public Mobile() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
