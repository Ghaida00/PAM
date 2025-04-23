package com.example.projectakhir.data;

import java.util.ArrayList;

public class Hewan {
    public String nama, kota, jenis, umur, gender, berat, deskripsi;
    public int gambarThumbnailResId;
    public int gambarDetailResId;
    public ArrayList<String> traits;

    public Hewan(String nama, String kota, String jenis, String umur, String gender, String berat,
                 String[] traitsArray, int gambarThumbnailResId, int gambarDetailResId, String deskripsi) {
        this.nama = nama;
        this.kota = kota;
        this.jenis = jenis;
        this.umur = umur;
        this.gender = gender;
        this.berat = berat;
        this.deskripsi = deskripsi;
        this.gambarThumbnailResId = gambarThumbnailResId;
        this.gambarDetailResId = gambarDetailResId;
        this.traits = new ArrayList<>();
        for (String t : traitsArray) this.traits.add(t);
    }
}
