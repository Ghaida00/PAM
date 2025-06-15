// com.example.projectakhir.data.model.User.java
package com.example.projectakhir.data.model;

import android.location.Address;

public class User {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String address; // NEW: Menambahkan properti address
    private String profileImageUrl; // NEW: Menambahkan properti profileImageUrl

    public User() {
        // Diperlukan untuk Firebase Firestore
    }

    // UPDATED: Konstruktor baru dengan address dan profileImageUrl
    public User(String uid, String firstName, String lastName, String email, String username, String address, String profileImageUrl) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}