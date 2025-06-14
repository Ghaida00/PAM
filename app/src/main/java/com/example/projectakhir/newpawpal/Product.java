package com.example.projectakhir.newpawpal;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private double rating; // Pastikan data rating ada di Firebase jika ingin ditampilkan
    private long price;
    private String category; // === Tambahan field
    private String description; // === Tambahan field
    private int stock; // === Tambahan field

    public Product() {
        // Diperlukan konstruktor kosong untuk Firebase Realtime Database
    }

    // Konstruktor dengan semua field (termasuk yang baru ditambahkan)
    public Product(String id, String name, String imageUrl, double rating, long price, String category, String description, int stock) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.price = price;
        this.category = category;
        this.description = description;
        this.stock = stock;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public long getPrice() {
        return price;
    }

    public String getCategory() { // === Getter untuk category
        return category;
    }

    public String getDescription() { // === Getter untuk description
        return description;
    }

    public int getStock() { // === Getter untuk stock
        return stock;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setCategory(String category) { // === Setter untuk category
        this.category = category;
    }

    public void setDescription(String description) { // === Setter untuk description
        this.description = description;
    }

    public void setStock(int stock) { // === Setter untuk stock
        this.stock = stock;
    }
}
