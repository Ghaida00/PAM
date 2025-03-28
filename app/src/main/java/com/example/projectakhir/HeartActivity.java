package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;

public class HeartActivity extends AppCompatActivity {

    FlexboxLayout groomingList, doctorList;
    Button btnGrooming, btnDoctor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        EdgeToEdge.enable(this);

        // Inisialisasi View
        groomingList = findViewById(R.id.groomingAppointmentContainer);
        doctorList = findViewById(R.id.doctorAppointmentContainer);
        btnGrooming = findViewById(R.id.btnGrooming);
        btnDoctor = findViewById(R.id.btnDoctor);

        // Event klik tombol
        btnGrooming.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroomingActivity.class);
            startActivity(intent);
        });

        btnDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorActivity.class);
            startActivity(intent);
        });

        // Dummy data grooming
        addAppointment(groomingList, "Grace", "Today", "15:00", "grooming", R.drawable.ic_spa);
        addAppointment(groomingList, "Grace", "Tomorrow", "14:00", "grooming", R.drawable.ic_self_care);
        addAppointment(groomingList, "Anomali", "Tomorrow", "12:00", "grooming", R.drawable.ic_spa);
        addAppointment(groomingList, "Amat", "Tomorrow", "16:00", "grooming", R.drawable.ic_cut);

        // Dummy data doctor
        addAppointment(doctorList, "Grace", "Today", "15:00", "doctor", R.drawable.ic_stethoscope);
        addAppointment(doctorList, "Grace", "Tomorrow", "14:00", "doctor", R.drawable.ic_medical_services);
        addAppointment(doctorList, "Anomali", "Tomorrow", "12:00", "doctor", R.drawable.ic_vaccines);
        addAppointment(doctorList, "Amat", "Tomorrow", "16:00", "doctor", R.drawable.ic_stethoscope);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fungsi tambah kartu appointment
    private void addAppointment(FlexboxLayout container, String nama, String hari, String jam, String tipe, int iconRes) {
        View view = getLayoutInflater().inflate(R.layout.item_appointment, container, false);

        TextView txtNama = view.findViewById(R.id.namaHewan);
        TextView txtWaktu = view.findViewById(R.id.jadwal);
        ImageView icon = view.findViewById(R.id.iconLayanan);
        LinearLayout background = view.findViewById(R.id.bgAppointment);

        txtNama.setText(nama);
        txtWaktu.setText(hari + " • " + jam);
        icon.setImageResource(iconRes);

        if (hari.equals("Today")) {
            background.setBackgroundResource(R.drawable.bg_tag_kuning);
        } else {
            background.setBackgroundResource(R.drawable.bg_tag_hijau);
        }

        container.addView(view);
    }
}