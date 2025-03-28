package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SalonDetailActivity extends AppCompatActivity {

    String nama, kota, jam;
    int gambarResId;
    ArrayList<String> layanan;

    String jenisHewanDipilih = "Dog";
    ArrayList<String> layananDipilih = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_detail);
        EdgeToEdge.enable(this);

        // Ambil data dari intent
        Intent i = getIntent();
        nama = i.getStringExtra("nama");
        kota = i.getStringExtra("kota");
        jam = i.getStringExtra("jam");
        gambarResId = i.getIntExtra("gambar", R.drawable.ic_launcher_foreground);
        layanan = (ArrayList<String>) i.getSerializableExtra("layanan");

        // View
        ImageView imgSalon = findViewById(R.id.imgSalon);
        TextView namaSalon = findViewById(R.id.namaSalon);
        TextView kotaSalon = findViewById(R.id.kotaSalon);
        TextView descSalon = findViewById(R.id.descSalon);
        LinearLayout pilihanLayanan = findViewById(R.id.layananContainer);

        // Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Gambar dan info
        imgSalon.setImageResource(gambarResId);
        namaSalon.setText(nama);
        kotaSalon.setText(kota);
        descSalon.setText("Tempat grooming terpercaya dengan pelayanan " + layanan.size() + " jenis layanan. Jam buka: " + jam);

        // Tampilkan layanan
        for (String l : layanan) {
            TextView tag = new TextView(this);
            tag.setText(l);
            tag.setTextSize(12f);
            tag.setPadding(24, 12, 24, 12);
            tag.setTextColor(Color.BLACK);
            tag.setBackgroundResource(R.drawable.bg_tag_kuning);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 24, 24);
            tag.setLayoutParams(lp);

            tag.setClickable(true);
            tag.setOnClickListener(v -> {
                if (layananDipilih.contains(l)) {
                    layananDipilih.remove(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_kuning);
                } else {
                    layananDipilih.add(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_hijau);
                }
            });

            pilihanLayanan.addView(tag);
        }

        findViewById(R.id.btnBook).setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("namaSalon", nama);
            intent.putExtra("layanan", layananDipilih);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}