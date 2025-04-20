package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DaftarHewanActivity extends AppCompatActivity {

    ArrayList<Hewan> semuaHewan = new ArrayList<>();
    ArrayList<Hewan> tampilkanHewan = new ArrayList<>();
    HewanAdapter adapter;
    String kotaTerpilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_hewan);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        kotaTerpilih = getIntent().getStringExtra("kota");

        // Data hewan lengkap
        semuaHewan.add(new Hewan("Anomali", "Jakarta", "Reptil", "1 Tahun", "Jantan", "1kg",
                new String[]{"Lazy", "Smart", "Spoiled"}, R.drawable.anomali, R.drawable.anomali_no_background,
                "Anomali suka rebahan di bawah lampu UV dan main petak umpet di sela batuan. Cocok buat kamu yang kalem, tapi sayang hewan unik."));

        semuaHewan.add(new Hewan("Amat", "Surabaya", "Reptil", "2 Tahun", "Jantan", "1kg",
                new String[]{"Silly", "Playful", "Smart"}, R.drawable.amat, R.drawable.amat_no_background,
                "Amat semangat banget tiap pagi! Dia suka eksplor kandang, bahkan kadang pura-pura ngilang. Kalo kamu butuh hewan aktif tapi low maintenance, Amat cocok banget."));

        semuaHewan.add(new Hewan("Agus", "Bali", "Anjing", "3 Tahun", "Jantan", "6kg",
                new String[]{"Silly", "Playful", "Friendly"}, R.drawable.agus, R.drawable.agus_no_background,
                "Agus selalu sedia buat diajak jalan-jalan atau sekadar nonton TV bareng. Dia bisa bikin kamu ketawa cuma dari ekspresi wajahnya."));

        semuaHewan.add(new Hewan("Grace", "Surabaya", "Kucing", "2 Tahun", "Betina", "4kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.grace, R.drawable.grace_no_background,
                "Grace manja parah. Suka nempel terus kayak perangko, tapi juga pinter buka lemari sendiri kalau laper."));

        semuaHewan.add(new Hewan("Grace", "Surabaya", "Kucing", "2 Tahun", "Betina", "4kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.grace, R.drawable.grace_no_background,
                "Grace manja parah. Suka nempel terus kayak perangko, tapi juga pinter buka lemari sendiri kalau laper."));

        semuaHewan.add(new Hewan("Claire", "Jakarta", "Anjing", "4 Tahun", "Jantan", "4.6kg",
                new String[]{"Smart", "Fearful", "Lazy"}, R.drawable.claire, R.drawable.claire_no_background,
                "Claire agak penakut sama suara keras, tapi sekali kenal kamu, dia bakal lengket banget. Suka duduk di kaki orang dan ngikutin ke mana pun."));

        semuaHewan.add(new Hewan("Bambang", "Yogyakarta", "Kucing", "3 Tahun", "Jantan", "4.6kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.bambang, R.drawable.bambang_no_background,
                "Bambang ditemukan duduk sendirian di depan warung, pura-pura kuat padahal pincang. Sekarang dia udah sembuh dan jago peluk bantal."));

        // Setup RecyclerView
        RecyclerView rv = findViewById(R.id.recyclerHewan);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HewanAdapter(this, tampilkanHewan);
        rv.setAdapter(adapter);

        // Tampilkan semua hewan sesuai kota
        filterHewan("Semua");

        // Setup filter kategori
        LinearLayout kategoriContainer = findViewById(R.id.kategoriContainer);
        String[] kategoriList = {"Semua", "Kucing", "Anjing", "Reptil", "Burung"};
        for (String kategori : kategoriList) {
            TextView txt = new TextView(this);
            txt.setText(kategori);
            txt.setTextSize(14f);
            txt.setPadding(32, 16, 32, 16);
            txt.setBackgroundResource(R.drawable.bg_tag_kuning);
            txt.setTextColor(Color.BLACK);
            txt.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(12, 0, 12, 0);
            txt.setLayoutParams(lp);

            txt.setOnClickListener(v -> filterHewan(kategori));
            kategoriContainer.addView(txt);
        }
    }

    private void filterHewan(String kategori) {
        tampilkanHewan.clear();
        for (Hewan h : semuaHewan) {
            if (h.kota.equals(kotaTerpilih) && (kategori.equals("Semua") || h.jenis.equals(kategori))) {
                tampilkanHewan.add(h);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
