package com.example.projectakhir;

public class CartItem {
    private String productId;
    private String name;
    private String imageUrl;
    private double price;
    private int quantity;

    public CartItem() {
        // Diperlukan untuk deserialisasi Firebase (jika Anda menyimpan keranjang di Firestore)
    }

    public CartItem(String productId, String name, String imageUrl, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Metode helper untuk menghitung total harga item ini
    public double getTotalItemPrice() {
        return price * quantity;
    }
}
