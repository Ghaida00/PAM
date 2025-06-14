package com.example.projectakhir;

public class Category {
    private String name;
    private boolean isSelected; // Untuk menandai kategori yang sedang aktif

    public Category() {
        // Konstruktor kosong untuk Firebase jika Anda menyimpannya di sana
    }

    public Category(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}