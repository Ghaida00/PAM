package com.example.projectakhir;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroomingActivity extends AppCompatActivity {

    LinearLayout kategoriContainer;
    RecyclerView recyclerSalon;
    SalonAdapter adapter;

    ArrayList<Salon> semuaSalon = new ArrayList<>();
    ArrayList<Salon> tampilkanSalon = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grooming);
        EdgeToEdge.enable(this);

        kategoriContainer = findViewById(R.id.kategoriContainer);
        recyclerSalon = findViewById(R.id.recyclerSalon);

        // Tombol back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Dummy salon
        semuaSalon.add(new Salon("Barber Pet", "Surabaya", "Open • Close at 20:00",
                new String[]{"Wash", "Brush", "Cut", "Spa"}, R.drawable.grace));

        semuaSalon.add(new Salon("Pet Zone", "Surabaya", "Open • Close at 18:00",
                new String[]{"Wash", "Spa"}, R.drawable.grace));

        semuaSalon.add(new Salon("Clean Tails", "Surabaya", "Open • Close at 21:00",
                new String[]{"Brush", "Cut"}, R.drawable.claire));

        // Setup RecyclerView
        recyclerSalon.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalonAdapter(this, tampilkanSalon);
        recyclerSalon.setAdapter(adapter);

        // Filter awal: Semua
        filterSalon("Semua");

        // Setup filter kategori
        String[] kategoriList = {"Semua", "Wash", "Brush", "Spa", "Cut"};
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

            txt.setOnClickListener(v -> filterSalon(kategori));
            kategoriContainer.addView(txt);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Filter salon berdasarkan layanan
    private void filterSalon(String kategori) {
        tampilkanSalon.clear();
        for (Salon s : semuaSalon) {
            if (kategori.equals("Semua") || s.menyediakanLayanan(kategori)) {
                tampilkanSalon.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }
}