package com.example.projectakhir.ui.homepage;

public class ExploreItem {
    private final String imageUrl;
    private final String title;
    private final String subtitle;
    private final String price;

    public ExploreItem(String imageUrl, String title, String subtitle, String price) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
    }

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getPrice() { return price; }
}