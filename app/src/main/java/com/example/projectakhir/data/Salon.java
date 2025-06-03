package com.example.projectakhir.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Salon {
    public String nama, kota, jam;

    /*public ArrayList<String> layanan;*/
    public int gambarResId;

    public List<String> layanan;

    public Salon(String nama, String kota, String jam, String[] layananArray, int gambarResId) {
        this.nama = nama;
        this.kota = kota;
        this.jam = jam;
        this.gambarResId = gambarResId;
        this.layanan = new ArrayList<>();
        if (layananArray != null) {
            this.layanan.addAll(Arrays.asList(layananArray));
        }
    }

    public boolean menyediakanLayanan(String kategori) {
        return layanan.contains(kategori);
    }

    public String getNama() {
        return nama;
    }

    public String getKota() {
        return kota;
    }

    public String getJam() {
        return jam;
    }

    public int getGambarResId() {
        return gambarResId;
    }

    public List<String> getLayanan() {
        return layanan;
    }
}
