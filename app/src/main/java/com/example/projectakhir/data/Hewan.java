package com.example.projectakhir.data;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

public class Hewan {
    private String id;
    private String nama;
    private String kota;
    private String jenis;
    private String umur;
    private String gender;
    private String berat;
    private String deskripsi;
    private String thumbnailImageUrl;
    private String detailImageUrl;
    private List<String> traits;
    @ServerTimestamp
    public Date timestamp;

    public Hewan() {
        this.traits = new ArrayList<>();
    }

    public Hewan(String nama, String kota, String jenis, String umur, String gender, String berat,
                 String[] traitsArray, String thumbnailImageUrl, String detailImageUrl, String deskripsi) {
        this.nama = nama;
        this.kota = kota;
        this.jenis = jenis;
        this.umur = umur;
        this.gender = gender;
        this.berat = berat;
        this.deskripsi = deskripsi;
        this.thumbnailImageUrl = thumbnailImageUrl; // Simpan URL
        this.detailImageUrl = detailImageUrl;       // Simpan URL
        this.traits = new ArrayList<>();
        if (traitsArray != null) {
            this.traits.addAll(Arrays.asList(traitsArray));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getUmur() {
        return umur;
    }

    public void setUmur(String umur) {
        this.umur = umur;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getDetailImageUrl() {
        return detailImageUrl;
    }

    public void setDetailImageUrl(String detailImageUrl) {
        this.detailImageUrl = detailImageUrl;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }

    public Date getTimestamp() { return timestamp; }
}