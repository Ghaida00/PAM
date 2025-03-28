package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DetailHewanActivity extends AppCompatActivity {

    String nama, kota, jenis, umur, gender, berat, deskripsi;
    int gambarResId;
    ArrayList<String> traits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hewan);

        // Ambil data dari intent
        Intent i = getIntent();
        nama = i.getStringExtra("nama");
        kota = i.getStringExtra("kota");
        jenis = i.getStringExtra("jenis");
        umur = i.getStringExtra("umur");
        gender = i.getStringExtra("gender");
        berat = i.getStringExtra("berat");
        deskripsi = i.getStringExtra("deskripsi");
        gambarResId = i.getIntExtra("gambar", R.drawable.ic_launcher_foreground);
        traits = (ArrayList<String>) i.getSerializableExtra("traits");

        // Inisialisasi View
        ImageView imgHewan = findViewById(R.id.imgHewan);
        TextView namaHewan = findViewById(R.id.namaHewan);
        TextView kotaHewan = findViewById(R.id.kotaHewan);
        TextView detailHewan = findViewById(R.id.detailHewan);
        TextView descHewan = findViewById(R.id.descHewan);
        LinearLayout traitsLayout = findViewById(R.id.traitsLayout);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        LinearLayout btnAdopt = findViewById(R.id.btnAdopt);
        btnAdopt.setOnClickListener(v -> {
            Intent form = new Intent(this, FormAdopsiActivity.class);
            form.putExtra("namaHewan", nama);
            startActivity(form);
        });

        // Set data tampilan
        imgHewan.setImageResource(gambarResId);
        namaHewan.setText(nama);
        kotaHewan.setText(kota);
        detailHewan.setText(gender + "  •  " + umur + "  •  " + berat);
        descHewan.setText(deskripsi);

        // Tampilkan traits secara dinamis
        traitsLayout.removeAllViews();

        if (traits.size() == 3) {
            traitsLayout.setOrientation(LinearLayout.VERTICAL);

            // Atas (1 icon)
            LinearLayout top = new LinearLayout(this);
            top.setGravity(Gravity.CENTER);
            top.addView(createTraitView(traits.get(0)));

            // Bawah (2 icon) dengan jarak horizontal lebih lebar
            LinearLayout bottom = new LinearLayout(this);
            bottom.setGravity(Gravity.CENTER);
            bottom.setPadding(0, 26, 0, 0);

            // Trait kiri
            LinearLayout traitLeft = createTraitView(traits.get(1));
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            leftParams.setMarginEnd(250);
            traitLeft.setLayoutParams(leftParams);

            // Trait kanan
            LinearLayout traitRight = createTraitView(traits.get(2));
            LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rightParams.setMarginStart(250);
            traitRight.setLayoutParams(rightParams);

            // Tambahkan ke layout bawah
            bottom.addView(traitLeft);
            bottom.addView(traitRight);

            // Tambahkan ke parent layout
            traitsLayout.addView(top);
            traitsLayout.addView(bottom);
    } else {
            // Grid biasa (3 per baris)
            traitsLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout currentRow = null;

            for (int y = 0; y < traits.size(); y++) {
                if (y % 3 == 0) {
                    currentRow = new LinearLayout(this);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setGravity(Gravity.CENTER);
                    currentRow.setPadding(0, 8, 0, 8);
                    traitsLayout.addView(currentRow);
                }

                if (currentRow != null) {
                    currentRow.addView(createTraitView(traits.get(y)));
                }
            }
        }
    }

    // Fungsi bikin satu icon trait
    private LinearLayout createTraitView(String trait) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(32, 8, 32, 8);
        layout.setLayoutParams(lp);

        ImageView icon = new ImageView(this);
        icon.setImageResource(getIconForTrait(trait));
        icon.setLayoutParams(new LinearLayout.LayoutParams(100, 64));

        TextView label = new TextView(this);
        label.setText(capitalize(trait));
        label.setTextSize(12f);
        label.setTypeface(null, android.graphics.Typeface.BOLD);
        label.setPadding(0, 4, 0, 0);

        layout.addView(icon);
        layout.addView(label);
        return layout;
    }

    // Icon sesuai trait
    private int getIconForTrait(String trait) {
        switch (trait.toLowerCase()) {
            case "friendly": return R.drawable.ic_paw;
            case "silly": return R.drawable.traits_cat;
            case "playful": return R.drawable.traits_toy;
            case "spoiled": return R.drawable.traits_spoiled;
            case "lazy": return R.drawable.traits_lazy;
            case "smart": return R.drawable.traits_smart;
            case "fearful": return R.drawable.traits_fearful;
            default: return R.drawable.ic_paw;
        }
    }

    // Kapitalisasi huruf depan
    private String capitalize(String input) {
        if (input == null || input.length() == 0) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
