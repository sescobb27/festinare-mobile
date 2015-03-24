package discount.up.hurry.hurryupdiscount.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Client {

    @SerializedName("_id")
    private String id;
    @SerializedName("client_plans")
    private String clientPlans;
    private String email;
    private String username;
    private String name;
    private float rate;
    @SerializedName("image_url")
    private String imageUrl;
    private List<String> addresses;

    // associations
    private List<Location> locations;
    private List<Discount> discounts;
    private List<Category> categories;

    public Client() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientPlans() {
        return clientPlans;
    }

    public void setClientPlans(String clientPlans) {
        this.clientPlans = clientPlans;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
