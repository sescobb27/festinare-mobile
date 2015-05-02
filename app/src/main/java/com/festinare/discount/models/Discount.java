package com.festinare.discount.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Discount implements Serializable {

    @SerializedName("_id")
    private String id;
    @SerializedName("discount_rate")
    private int discountRate;
    private String title;
    @SerializedName("secret_key")
    private String secretKey;
    private int duration;
    @SerializedName("duration_term")
    private String durationTerm;
    private List<String> hashtags;

    // associations
    private List<Category> categories;

    public Discount() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationTerm() {
        return durationTerm;
    }

    public void setDurationTerm(String durationTerm) {
        this.durationTerm = durationTerm;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
