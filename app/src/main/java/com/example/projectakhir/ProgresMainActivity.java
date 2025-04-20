package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ProgresMainActivity extends AppCompatActivity {

    Button btnLihatProgresAdopsi, btnLihatProgresPengaduan;
    ImageView btnBackProgresMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progres_main);

        btnLihatProgresAdopsi = findViewById(R.id.btnLihatProgresAdopsi);
        btnLihatProgresPengaduan = findViewById(R.id.btnLihatProgresPengaduan);
        btnBackProgresMain = findViewById(R.id.btnBackProgresMain);

        btnBackProgresMain.setOnClickListener(v -> finish());

        btnLihatProgresAdopsi.setOnClickListener(v -> {
            Intent intent = new Intent(ProgresMainActivity.this, ProgresAdopsiActivity.class);
            startActivity(intent);
        });

        btnLihatProgresPengaduan.setOnClickListener(v -> {
            Intent intent = new Intent(ProgresMainActivity.this, ProgresPengaduanActivity.class);
            startActivity(intent);
        });
    }
}