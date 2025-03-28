package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GridLayout gridKota;
    LinearLayout listBaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridKota = findViewById(R.id.gridKota);
        listBaru = findViewById(R.id.listBaru);
        ImageView navHeart = findViewById(R.id.navHeart);
        navHeart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HeartActivity.class);
            startActivity(intent);
        });
//        navHome.setOnClickListener(v -> {
//            // stay di MainActivity atau refresh
//        });
//
//        navPaw.setOnClickListener(v -> {
//            // Intent ke fitur adopsi
//        });
//
//        navHeart.setOnClickListener(v -> {
//            // Intent ke fitur Grooming & Doctor
//        });
//
//        navShop.setOnClickListener(v -> {
//
//        });
//
//        navFood.setOnClickListener(v -> {
//
//        });

        // Grid Kota (Rounded Pastel Card)
        tambahKota("Jakarta", R.drawable.jakarta, R.drawable.bg_kota_jakarta);
        tambahKota("Surabaya", R.drawable.surabaya, R.drawable.bg_kota_surabaya);
        tambahKota("Yogyakarta", R.drawable.yogyakarta, R.drawable.bg_kota_yogyakarta);
        tambahKota("Bali", R.drawable.bali, R.drawable.bg_kota_bali);

        // Hewan Baru Ditambahkan (Card Rounded + Tag)
        // Anggap datanya sama dengan di DaftarHewanActivity
        tambahHewanBaru(new Hewan("Grace", "Surabaya", "Kucing", "2 Tahun", "Betina", "4kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.grace, R.drawable.grace_no_background,
                "Grace manja parah. Suka nempel terus kayak perangko, tapi juga pinter buka lemari sendiri kalau laper."));

        tambahHewanBaru(new Hewan("Claire", "Jakarta", "Anjing", "4 Tahun", "Jantan", "4.6kg",
                new String[]{"Smart", "Fearful", "Lazy"}, R.drawable.claire, R.drawable.claire_no_background,
                "Claire agak penakut sama suara keras, tapi sekali kenal kamu, dia bakal lengket banget. Suka duduk di kaki orang dan ngikutin ke mana pun."));
    }

    private void tambahKota(String nama, int iconResId, int bgDrawableResId) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setBackgroundResource(bgDrawableResId);
        card.setGravity(android.view.Gravity.CENTER);
        card.setElevation(8f);
        card.setClickable(true);
        card.setFocusable(true);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconResId);
        icon.setLayoutParams(new LinearLayout.LayoutParams(96, 96));

        TextView label = new TextView(this);
        label.setText(nama);
        label.setTextSize(16f);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(android.view.Gravity.CENTER);

        card.addView(icon);
        card.addView(label);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int biggerWidth = (int) (metrics.widthPixels * 0.42); // Lebih besar
        int biggerHeight = (int) (metrics.widthPixels * 0.42); // Kotak besar

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = biggerWidth;
        params.height = biggerHeight;
        params.setMargins(24, 24, 24, 24);
        card.setLayoutParams(params);

        // Tambah padding juga biar lebih lega
        card.setPadding(48, 48, 48, 48);


        card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DaftarHewanActivity.class);
            intent.putExtra("kota", nama);
            startActivity(intent);
        });

        gridKota.addView(card);
    }

    private void tambahHewanBaru(Hewan h) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_hewan_rounded);
        card.setElevation(8f);
        card.setPadding(24, 24, 24, 24);

        ImageView img = new ImageView(this);
        img.setImageResource(h.gambarThumbnailResId);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setClipToOutline(true);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                380  // tinggi tetap, lebar menyesuaikan card
        );
        img.setLayoutParams(imgParams);


        TextView namaHewan = new TextView(this);
        namaHewan.setText(h.nama);
        namaHewan.setTextSize(14f);
        namaHewan.setTypeface(null, Typeface.BOLD);
        namaHewan.setPadding(12, 8, 12, 4);

        TextView info = new TextView(this);
        info.setText(h.kota + "  •  " + h.gender);
        info.setTextSize(12f);
        info.setPadding(12, 0, 12, 4);

        // TAG 1 - Umur
        TextView tagUmur = new TextView(this);
        tagUmur.setText(h.umur);
        tagUmur.setTextSize(12f);
        tagUmur.setTextColor(Color.BLACK);
        tagUmur.setPadding(24, 8, 24, 8);
        tagUmur.setBackgroundResource(R.drawable.bg_tag_kuning);

        // TAG 2 - Gender
        TextView tagGender = new TextView(this);
        tagGender.setText(h.gender);
        tagGender.setTextSize(12f);
        tagGender.setTextColor(Color.WHITE);
        tagGender.setPadding(24, 8, 24, 8);
        tagGender.setBackgroundResource(R.drawable.bg_tag_hijau);

        // Layout tag
        LinearLayout tagLayout = new LinearLayout(this);
        tagLayout.setOrientation(LinearLayout.HORIZONTAL);
        tagLayout.setPadding(12, 4, 12, 12);
        tagLayout.addView(tagUmur);
        tagLayout.addView(tagGender);

        card.addView(img);
        card.addView(namaHewan);
        card.addView(info);
        card.addView(tagLayout);

        // Biar bisa di-click → buka DetailHewanActivity
        card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailHewanActivity.class);
            intent.putExtra("nama", h.nama);
            intent.putExtra("kota", h.kota);
            intent.putExtra("jenis", h.jenis);
            intent.putExtra("umur", h.umur);
            intent.putExtra("gender", h.gender);
            intent.putExtra("berat", h.berat);
            intent.putExtra("gambar", h.gambarDetailResId); // pakai yang transparan
            intent.putExtra("deskripsi", h.deskripsi);
            intent.putExtra("traits", h.traits); // Serializable
            startActivity(intent);
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                380,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(12, 0, 12, 0);
        card.setLayoutParams(lp);

        listBaru.addView(card);
    }
}