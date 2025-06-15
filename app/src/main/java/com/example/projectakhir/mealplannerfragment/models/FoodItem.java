package com.example.projectakhir.mealplannerfragment.models;

public class FoodItem {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private int calories;
    private String category; // Breakfast, Lunch, Dinner, Snack

    private Boolean isRecommended;
    private Boolean custom;
    private String createdBy;
    private String day;

    public FoodItem() {
    }

    public FoodItem(String id, String name, String description, String imageUrl, int calories, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.calories = calories;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Boolean getIsRecommended() { return isRecommended; }
    public void setIsRecommended(Boolean isRecommended) { this.isRecommended = isRecommended; }

    public Boolean getCustom() { return custom; }
    public void setCustom(Boolean custom) { this.custom = custom; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return id != null && id.equals(foodItem.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}