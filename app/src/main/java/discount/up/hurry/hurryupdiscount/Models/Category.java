package discount.up.hurry.hurryupdiscount.Models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("_id")
    private String id;
    private String name;
    private String description;

    public Category() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}