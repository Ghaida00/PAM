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

import java.util.ArrayList; // Import ArrayList

public class HeartActivity extends AppCompatActivity {

    FlexboxLayout groomingList, doctorList;
    Button btnGrooming, btnDoctor;

    // Contoh list untuk menyimpan data appointment lengkap
    ArrayList<AppointmentData> groomingAppointments = new ArrayList<>();
    ArrayList<AppointmentData> doctorAppointments = new ArrayList<>();


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

        // --- Buat Data Dummy Lengkap ---
        setupDummyData(); // Panggil fungsi untuk mengisi data dummy

        // Tampilkan appointment dari data dummy
        displayAppointments();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fungsi untuk mengisi data dummy lengkap
    private void setupDummyData() {
        // Dummy data grooming (dengan detail tambahan)
        groomingAppointments.add(new AppointmentData("Grace", "Today", "15:00",
                "Barber Pet", "Jl. Mulyorejo No. 10, Surabaya", "Grooming", "Spa - 1 Hour Course",
                "Kucing", "Note: Nakal Jahil dan Gasuka dipegang perutnya", R.drawable.ic_spa));
        groomingAppointments.add(new AppointmentData("Grace", "Tomorrow", "14:00",
                "Pet Zone", "Jl. Kertajaya Indah V/1, Surabaya", "Grooming", "Wash Only",
                "Kucing", "Suka main air", R.drawable.ic_self_care)); // Sesuaikan icon jika perlu
        groomingAppointments.add(new AppointmentData("Anomali", "Tomorrow", "12:00",
                "Barber Pet", "Jl. Mulyorejo No. 10, Surabaya", "Grooming", "Basic Grooming",
                "Reptil", "-", R.drawable.ic_spa)); // Sesuaikan icon jika perlu
        groomingAppointments.add(new AppointmentData("Amat", "Tomorrow", "16:00",
                "Clean Tails", "Jl. Dharmahusada No. 112, Surabaya", "Grooming", "Cut & Style",
                "Reptil", "Tenang", R.drawable.ic_cut));

        // Dummy data doctor (dengan detail tambahan)
        doctorAppointments.add(new AppointmentData("Grace", "Today", "15:00",
                "Paw Vet", "Jl. Raya Darmo Permai III, Surabaya", "Doctor", "Check-up Rutin",
                "Kucing", "Agak takut orang baru", R.drawable.ic_stethoscope));
        doctorAppointments.add(new AppointmentData("Grace", "Tomorrow", "14:00",
                "Happy Paw Clinic", "Jl. Manyar Kertoarjo VII/2, Surabaya", "Doctor", "Vaksin Tahunan",
                "Kucing", "-", R.drawable.ic_medical_services)); // Sesuaikan icon jika perlu
        doctorAppointments.add(new AppointmentData("Anomali", "Tomorrow", "12:00",
                "VetCare Center", "Jl. Ngagel Jaya Selatan No. 8, Surabaya", "Doctor", "Konsultasi Kulit",
                "Reptil", "Bawa data sebelumnya", R.drawable.ic_vaccines)); // Sesuaikan icon jika perlu
        doctorAppointments.add(new AppointmentData("Amat", "Tomorrow", "16:00",
                "Paw Vet", "Jl. Raya Darmo Permai III, Surabaya", "Doctor", "Pemeriksaan Mata",
                "Reptil", "-", R.drawable.ic_stethoscope));
    }

    // Fungsi untuk menampilkan semua appointment
    private void displayAppointments() {
        groomingList.removeAllViews(); // Bersihkan list sebelum menambah
        doctorList.removeAllViews();

        for (AppointmentData data : groomingAppointments) {
            addAppointmentView(groomingList, data);
        }
        for (AppointmentData data : doctorAppointments) {
            addAppointmentView(doctorList, data);
        }
    }


    // --- MODIFIKASI addAppointment MENJADI addAppointmentView ---
    // Menerima objek AppointmentData lengkap
    private void addAppointmentView(FlexboxLayout container, AppointmentData data) {
        View view = getLayoutInflater().inflate(R.layout.item_appointment, container, false);

        TextView txtNama = view.findViewById(R.id.namaHewan);
        TextView txtWaktu = view.findViewById(R.id.jadwal);
        ImageView icon = view.findViewById(R.id.iconLayanan);
        LinearLayout background = view.findViewById(R.id.bgAppointment);

        txtNama.setText(data.petName);
        txtWaktu.setText(data.appointmentDay + " • " + data.appointmentTime); // Tetap sama
        icon.setImageResource(data.iconRes); // Tetap sama

        // Atur background berdasarkan hari
        if (data.appointmentDay.equals("Today")) {
            background.setBackgroundResource(R.drawable.bg_tag_kuning);
        } else {
            background.setBackgroundResource(R.drawable.bg_tag_hijau);
        }

        // --- INI BAGIAN PENTING: OnClickListener untuk ke Detail ---
        view.setOnClickListener(v -> {
            Intent detailIntent = new Intent(HeartActivity.this, AppointmentDetailActivity.class);

            // Kirim SEMUA data detail menggunakan putExtra
            detailIntent.putExtra("serviceType", data.serviceType);
            detailIntent.putExtra("serviceDetails", data.serviceDetails);
            detailIntent.putExtra("providerName", data.providerName);
            detailIntent.putExtra("providerAddress", data.providerAddress);
            detailIntent.putExtra("appointmentDateTime", data.appointmentDay + " - " + data.appointmentTime); // Kirim gabungan
            detailIntent.putExtra("petNameType", data.petName + " - " + data.petType); // Kirim gabungan
            detailIntent.putExtra("petNotes", data.petNotes);
            detailIntent.putExtra("iconRes", data.iconRes);
            // Tambahkan data lain jika ada (misal: ID appointment, status, dll)

            startActivity(detailIntent);
        });
        // --- Akhir OnClickListener ---

        container.addView(view);
    }
}