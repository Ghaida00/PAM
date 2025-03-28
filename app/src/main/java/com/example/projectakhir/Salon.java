package com.example.projectakhir;

import java.util.ArrayList;

public class Salon {
    public String nama, kota, jam;
    public ArrayList<String> layanan;
    public int gambarResId;

    public Salon(String nama, String kota, String jam, String[] layananArray, int gambarResId) {
        this.nama = nama;
        this.kota = kota;
        this.jam = jam;
        this.gambarResId = gambarResId;
        this.layanan = new ArrayList<>();
        for (String s : layananArray) this.layanan.add(s);
    }

    public boolean menyediakanLayanan(String kategori) {
        return layanan.contains(kategori);
    }
}
