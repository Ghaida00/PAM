package com.example.projectakhir.data.repository;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserReport {
    private String id;
    private String namaPelapor;
    private String jenisHewan;
    private String alamatLokasi;
    private String status;
    private String userId;
    @ServerTimestamp
    public Date timestamp;

    // TAMBAHKAN KONSTRUKTOR KOSONG INI
    public UserReport() {
        // Konstruktor kosong diperlukan oleh Firestore
    }

    public UserReport(String id, String namaPelapor, String jenisHewan, String alamatLokasi, String status, String userId) {
        this.id = id;
        this.namaPelapor = namaPelapor;
        this.jenisHewan = jenisHewan;
        this.alamatLokasi = alamatLokasi;
        this.status = status;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaPelapor() {
        return namaPelapor;
    }

    public String getJenisHewan() {
        return jenisHewan;
    }

    public String getAlamatLokasi() {
        return alamatLokasi;
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