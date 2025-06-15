package com.example.projectakhir.data;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class AdoptionRequest {
    private String id; // ID dokumen Firestore
    private String namaHewan;
    private String jenisHewan;
    private String kotaPengambilan;
    private String status;
    private String userId;
    @ServerTimestamp
    public Date timestamp;

    // TAMBAHKAN KONSTRUKTOR KOSONG INI
    public AdoptionRequest() {
        // Konstruktor kosong diperlukan oleh Firestore
    }

    public AdoptionRequest(String id, String namaHewan, String jenisHewan, String kotaPengambilan, String status, String userId) {
        this.id = id;
        this.namaHewan = namaHewan;
        this.jenisHewan = jenisHewan;
        this.kotaPengambilan = kotaPengambilan;
        this.status = status;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaHewan() {
        return namaHewan;
    }

    public String getJenisHewan() {
        return jenisHewan;
    }

    public String getKotaPengambilan() {
        return kotaPengambilan;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}