package com.example.projectakhir.data.model;
import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private String imageUrl;
    private int stock;
    private String category;
    private String description;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Product() {
        // Diperlukan untuk Firebase Realtime Database
    }

    public Product(String id, String name, double price, String imageUrl, int stock, String category,String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.category = category;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}