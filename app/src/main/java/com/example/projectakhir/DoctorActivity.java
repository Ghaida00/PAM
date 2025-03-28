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

public class DoctorActivity extends AppCompatActivity {

    LinearLayout kategoriContainer;
    RecyclerView recyclerVet;
    SalonAdapter adapter;

    ArrayList<Salon> semuaVet = new ArrayList<>();
    ArrayList<Salon> tampilkanVet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        EdgeToEdge.enable(this);

        kategoriContainer = findViewById(R.id.kategoriContainer);
        recyclerVet = findViewById(R.id.recyclerVet);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Dummy data vet
        semuaVet.add(new Salon("Paw Vet", "Surabaya", "Open • Close at 20:00",
                new String[]{"Vaccine", "GCU", "Medicine"}, R.drawable.grace));

        semuaVet.add(new Salon("Happy Paw Clinic", "Surabaya", "Open • Close at 18:00",
                new String[]{"Check-up", "Vaccine"}, R.drawable.anomali));

        semuaVet.add(new Salon("VetCare Center", "Surabaya", "Open • Close at 21:00",
                new String[]{"Emergency", "Medicine"}, R.drawable.amat));

        recyclerVet.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalonAdapter(this, tampilkanVet, "doctor");

        recyclerVet.setAdapter(adapter);

        filterVet("Semua");

        String[] kategoriList = {"Semua", "Vaccine", "GCU", "Medicine", "Check-up", "Emergency"};
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

            txt.setOnClickListener(v -> filterVet(kategori));
            kategoriContainer.addView(txt);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterVet(String kategori) {
        tampilkanVet.clear();
        for (Salon s : semuaVet) {
            if (kategori.equals("Semua") || s.menyediakanLayanan(kategori)) {
                tampilkanVet.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }
}