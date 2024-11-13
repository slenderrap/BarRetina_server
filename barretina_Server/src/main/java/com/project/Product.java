package com.project;

import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private ArrayList<String> tags;
    private String imagePath;
    private String imageBase64;

    public Product(int id, String name, double price, String description, ArrayList<String> tags, String imagePath, String imageBase64) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.tags = tags;
        this.imagePath = imagePath;
        this.imageBase64 = imageBase64;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        JSONArray tagsJson = new JSONArray(tags);
        json.put("tags", tagsJson);
        json.put("price", price);
        json.put("description", description);
        json.put("imageBase64", imageBase64);
        return json;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }
}