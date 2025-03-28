package com.example.projectakhir;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Consumer;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    TextView namaSalon;
    LinearLayout tanggalContainer;
    FlexboxLayout waktuContainer;
    Button btnConfirm;

    String tanggalDipilih = "";
    String waktuDipilih = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        EdgeToEdge.enable(this);

        namaSalon = findViewById(R.id.txtNamaSalon);
        tanggalContainer = findViewById(R.id.tanggalContainer);
        waktuContainer = findViewById(R.id.waktuContainer);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Ambil data dari intent
        String nama = getIntent().getStringExtra("namaSalon");
        namaSalon.setText(nama);

        // Tombol back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Dummy tanggal
        ArrayList<String> tanggalList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdfHari = new SimpleDateFormat("EEE", Locale.ENGLISH); // Mon, Tue, etc.
        SimpleDateFormat sdfTanggal = new SimpleDateFormat("d", Locale.ENGLISH); // 1, 2, 3, ...

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 28–31 tergantung bulan

        for (int i = 1; i <= maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String hari = sdfHari.format(calendar.getTime());
            String tanggal = sdfTanggal.format(calendar.getTime());
            tanggalList.add(hari + ", " + tanggal);
        }

        for (String tgl : tanggalList) {
            TextView t = createTag(tgl, tanggalContainer, selected -> {
                tanggalDipilih = selected;
            });
            tanggalContainer.addView(t);
        }

        // Dummy jam
        String[] waktuList = {"09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00"};
        for (String jam : waktuList) {
            TextView j = createTag(jam, waktuContainer, selected -> {
                waktuDipilih = selected;
            });
            waktuContainer.addView(j);
        }

        // Confirm
        btnConfirm.setOnClickListener(v -> {
            if (tanggalDipilih.isEmpty() || waktuDipilih.isEmpty()) {
                Toast.makeText(this, "Pilih tanggal dan waktu dulu ya!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Booking berhasil! 🎉", Toast.LENGTH_LONG).show();
                finish(); // atau balik ke HeartActivity
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fungsi buat tag interaktif
    private TextView createTag(String text, ViewGroup parent, Consumer<String> onSelect) {
        TextView tag = new TextView(this);
        tag.setText(text);
        tag.setTextSize(14f);
        tag.setPadding(32, 16, 32, 16);
        tag.setBackgroundResource(R.drawable.bg_tag_kuning);
        tag.setTextColor(Color.BLACK);
        tag.setTypeface(null, Typeface.BOLD);
        tag.setClickable(true);

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12, 12, 12, 12);
        tag.setLayoutParams(params);

        tag.setOnClickListener(v -> {
            // Reset semua dulu
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child instanceof TextView) {
                    child.setBackgroundResource(R.drawable.bg_tag_kuning);
                }
            }
            // Aktifkan yang dipilih
            tag.setBackgroundResource(R.drawable.bg_tag_hijau);
            onSelect.accept(text);
        });

        return tag;
    }
}