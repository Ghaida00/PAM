// In com.example.projectakhir.data.repository.AdoptionRepository.java
package com.example.projectakhir.data.repository;

import com.example.projectakhir.R; // Import your R file
import com.example.projectakhir.data.Hewan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Using Java 8 streams for filtering

public class AdoptionRepository {

    private ArrayList<Hewan> allHewan = new ArrayList<>();

    public AdoptionRepository() {
        // Initialize dummy data here (moved from DaftarHewanActivity)
        setupDummyHewan();
    }

    private void setupDummyHewan() {
        // --- MOVE THE DUMMY DATA CREATION FROM DaftarHewanActivity HERE ---
        allHewan.add(new Hewan("Anomali", "Jakarta", "Reptil", "1 Tahun", "Jantan", "1kg",
                new String[]{"Lazy", "Smart", "Spoiled"}, R.drawable.anomali, R.drawable.anomali_no_background,
                "Anomali suka rebahan di bawah lampu UV dan main petak umpet di sela batuan. Cocok buat kamu yang kalem, tapi sayang hewan unik."));
        allHewan.add(new Hewan("Amat", "Surabaya", "Reptil", "2 Tahun", "Jantan", "1kg",
                new String[]{"Silly", "Playful", "Smart"}, R.drawable.amat, R.drawable.amat_no_background,
                "Amat semangat banget tiap pagi! Dia suka eksplor kandang, bahkan kadang pura-pura ngilang. Kalo kamu butuh hewan aktif tapi low maintenance, Amat cocok banget."));
        allHewan.add(new Hewan("Agus", "Bali", "Anjing", "3 Tahun", "Jantan", "6kg",
                new String[]{"Silly", "Playful", "Friendly"}, R.drawable.agus, R.drawable.agus_no_background,
                "Agus selalu sedia buat diajak jalan-jalan atau sekadar nonton TV bareng. Dia bisa bikin kamu ketawa cuma dari ekspresi wajahnya."));
        allHewan.add(new Hewan("Grace", "Surabaya", "Kucing", "2 Tahun", "Betina", "4kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.grace, R.drawable.grace_no_background,
                "Grace manja parah. Suka nempel terus kayak perangko, tapi juga pinter buka lemari sendiri kalau laper."));
        allHewan.add(new Hewan("Claire", "Jakarta", "Anjing", "4 Tahun", "Jantan", "4.6kg",
                new String[]{"Smart", "Fearful", "Lazy"}, R.drawable.claire, R.drawable.claire_no_background,
                "Claire agak penakut sama suara keras, tapi sekali kenal kamu, dia bakal lengket banget. Suka duduk di kaki orang dan ngikutin ke mana pun."));
        allHewan.add(new Hewan("Bambang", "Yogyakarta", "Kucing", "3 Tahun", "Jantan", "4.6kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.bambang, R.drawable.bambang_no_background,
                "Bambang ditemukan duduk sendirian di depan warung, pura-pura kuat padahal pincang. Sekarang dia udah sembuh dan jago peluk bantal."));
        // Add other dummy Hewan objects...
    }

    // Method to get filtered animals
    public List<Hewan> getFilteredHewan(String kota, String kategori) {
        // Use Java 8 Streams for cleaner filtering
        return allHewan.stream()
                .filter(h -> h.kota.equals(kota))
                .filter(h -> kategori.equals("Semua") || h.jenis.equalsIgnoreCase(kategori))
                .collect(Collectors.toList());
    }

    // Optional: Method to get all animals for a city (if needed)
    public List<Hewan> getAllHewanByCity(String kota) {
        return allHewan.stream()
                .filter(h -> h.kota.equals(kota))
                .collect(Collectors.toList());
    }
}