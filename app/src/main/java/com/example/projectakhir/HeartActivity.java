package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
// Hapus import FlexboxLayout jika tidak dipakai lagi
// import com.google.android.flexbox.FlexboxLayout;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class HeartActivity extends AppCompatActivity {

    // GANTI FlexboxLayout ke RecyclerView
    RecyclerView recyclerGroomingAppointments, recyclerDoctorAppointments;
    Button btnGrooming, btnDoctor;

    // Tambahkan variabel untuk Adapter
    AppointmentAdapter groomingAdapter;
    AppointmentAdapter doctorAdapter;

    // List untuk menyimpan data appointment lengkap (tetap sama)
    ArrayList<AppointmentData> groomingAppointments = new ArrayList<>();
    ArrayList<AppointmentData> doctorAppointments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Hapus atau sesuaikan EdgeToEdge jika menyebabkan masalah layout dengan RecyclerView
        setContentView(R.layout.activity_heart);


        // Inisialisasi View (RecyclerView)
        recyclerGroomingAppointments = findViewById(R.id.recyclerGroomingAppointments);
        recyclerDoctorAppointments = findViewById(R.id.recyclerDoctorAppointments);
        btnGrooming = findViewById(R.id.btnGrooming);
        btnDoctor = findViewById(R.id.btnDoctor);

        // Event klik tombol (tetap sama)
        btnGrooming.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroomingActivity.class);
            startActivity(intent);
        });

        btnDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorActivity.class);
            startActivity(intent);
        });

        // Buat Data Dummy Lengkap (tetap sama)
        setupDummyData();

        // --- Setup RecyclerView dan Adapter ---
        setupRecyclerViews();


       /* // Sesuaikan atau hapus listener WindowInsets jika EdgeToEdge diubah
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
    }

    // Fungsi setupDummyData (tetap sama seperti sebelumnya)
    private void setupDummyData() {
        // ... (Isi data dummy ke groomingAppointments dan doctorAppointments seperti sebelumnya) ...
        groomingAppointments.add(new AppointmentData("Grace", "Today", "15:00",
                "Barber Pet", "Jl. Mulyorejo No. 10, Surabaya", "Grooming", "Spa - 1 Hour Course",
                "Kucing", "Note: Nakal Jahil dan Gasuka dipegang perutnya", R.drawable.ic_spa));
        groomingAppointments.add(new AppointmentData("Grace", "Tomorrow", "14:00",
                "Pet Zone", "Jl. Kertajaya Indah V/1, Surabaya", "Grooming", "Wash Only",
                "Kucing", "Suka main air", R.drawable.ic_self_care));
        groomingAppointments.add(new AppointmentData("Anomali", "Tomorrow", "12:00",
                "Barber Pet", "Jl. Mulyorejo No. 10, Surabaya", "Grooming", "Basic Grooming",
                "Reptil", "-", R.drawable.ic_spa));
        groomingAppointments.add(new AppointmentData("Amat", "Tomorrow", "16:00",
                "Clean Tails", "Jl. Dharmahusada No. 112, Surabaya", "Grooming", "Cut & Style",
                "Reptil", "Tenang", R.drawable.ic_cut));

        doctorAppointments.add(new AppointmentData("Grace", "Today", "15:00",
                "Paw Vet", "Jl. Raya Darmo Permai III, Surabaya", "Doctor", "Check-up Rutin",
                "Kucing", "Agak takut orang baru", R.drawable.ic_stethoscope));
        doctorAppointments.add(new AppointmentData("Grace", "Tomorrow", "14:00",
                "Happy Paw Clinic", "Jl. Manyar Kertoarjo VII/2, Surabaya", "Doctor", "Vaksin Tahunan",
                "Kucing", "-", R.drawable.ic_medical_services));
        doctorAppointments.add(new AppointmentData("Anomali", "Tomorrow", "12:00",
                "VetCare Center", "Jl. Ngagel Jaya Selatan No. 8, Surabaya", "Doctor", "Konsultasi Kulit",
                "Reptil", "Bawa data sebelumnya", R.drawable.ic_vaccines));
        doctorAppointments.add(new AppointmentData("Amat", "Tomorrow", "16:00",
                "Paw Vet", "Jl. Raya Darmo Permai III, Surabaya", "Doctor", "Pemeriksaan Mata",
                "Reptil", "-", R.drawable.ic_stethoscope));
    }

    // --- Fungsi Baru untuk Setup RecyclerViews ---
    private void setupRecyclerViews() {
        // Setup RecyclerView Grooming
        groomingAdapter = new AppointmentAdapter(this, groomingAppointments);
        recyclerGroomingAppointments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerGroomingAppointments.setAdapter(groomingAdapter);

        // Setup RecyclerView Doctor
        doctorAdapter = new AppointmentAdapter(this, doctorAppointments);
        recyclerDoctorAppointments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerDoctorAppointments.setAdapter(doctorAdapter);
    }

    // --- HAPUS FUNGSI displayAppointments() dan addAppointmentView() YANG LAMA ---
    // private void displayAppointments() { ... }
    // private void addAppointmentView(...) { ... }

}