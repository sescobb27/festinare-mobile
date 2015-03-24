package discount.up.hurry.hurryupdiscount.Models;

import com.google.gson.annotations.SerializedName;

public class Mobile {

    @SerializedName("_id")
    private String id;
    private String token; // GCM_ID
    private final static String platform = "android";

    public Mobile() { }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static String getPlatform() {
        return platform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
