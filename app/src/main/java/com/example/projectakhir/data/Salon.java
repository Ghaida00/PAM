package com.example.projectakhir.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Salon {
    private String id; // Untuk menyimpan ID dokumen dari Firestore
    private String nama;
    private String kota;
    private String jam;
    private String tipe; // "grooming" atau "doctor"
    private String imageUrl; // Untuk menyimpan URL gambar dari Firebase Storage
    private List<String> layanan;

    // Diperlukan constructor kosong untuk deserialisasi Firestore
    public Salon() {
        this.layanan = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getKota() { return kota; }
    public String getJam() { return jam; }
    public String getTipe() { return tipe; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getLayanan() { return layanan; }

    // Setters (diperlukan oleh Firestore)
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setKota(String kota) { this.kota = kota; }
    public void setJam(String jam) { this.jam = jam; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setLayanan(List<String> layanan) { this.layanan = layanan; }

    public boolean menyediakanLayanan(String kategori) {
        if (this.layanan == null) return false;
        return layanan.contains(kategori);
    }
}