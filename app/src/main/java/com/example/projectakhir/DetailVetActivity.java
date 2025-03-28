package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class DetailVetActivity extends AppCompatActivity {

    String nama, kota, jam;
    int gambarResId;
    ArrayList<String> layanan;

    String jenisHewanDipilih = "Dog";
    ArrayList<String> layananDipilih = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vet);
        EdgeToEdge.enable(this);

        // Ambil data dari intent
        Intent i = getIntent();
        nama = i.getStringExtra("nama");
        kota = i.getStringExtra("kota");
        jam = i.getStringExtra("jam");
        gambarResId = i.getIntExtra("gambar", R.drawable.ic_launcher_foreground);
        layanan = (ArrayList<String>) i.getSerializableExtra("layanan");

        // View binding
        ImageView imgVet = findViewById(R.id.imgVet);
        TextView namaVet = findViewById(R.id.namaVet);
        TextView kotaVet = findViewById(R.id.kotaVet);
        TextView descVet = findViewById(R.id.descVet);
        FlexboxLayout layananContainer = findViewById(R.id.layananContainer);
        Button btnBook = findViewById(R.id.btnBook);

        // Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Set data
        imgVet.setImageResource(gambarResId);
        namaVet.setText(nama);
        kotaVet.setText(kota);
        descVet.setText("Klinik hewan terpercaya untuk kesehatan anakbumu. Pemeriksaan dilakukan oleh dokter berpengalaman dengan penanganan penuh kasih. Karena mereka pantas mendapatkan perawatan terbaik. Buka: " + jam);

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

            layananContainer.addView(tag);
        }

        // Book now
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("namaSalon", nama); // boleh kamu ganti jadi namaVet kalau ingin lebih tepat
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