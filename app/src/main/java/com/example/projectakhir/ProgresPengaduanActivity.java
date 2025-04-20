package com.example.projectakhir;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface; // Import DialogInterface

public class ProgresPengaduanActivity extends AppCompatActivity {

    TextView txtNamaPelaporProgres, txtJenisHewanPengaduanProgres, txtAlamatPengaduanProgres, txtStatusProgresPengaduan;
    Button btnBatalkanPengaduan;
    ImageView btnBackProgresPengaduan;
    String currentStatusPengaduan = ""; // Variabel untuk menyimpan status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progres_pengaduan);

        txtNamaPelaporProgres = findViewById(R.id.txtNamaPelaporProgres);
        txtJenisHewanPengaduanProgres = findViewById(R.id.txtJenisHewanPengaduanProgres);
        txtAlamatPengaduanProgres = findViewById(R.id.txtAlamatPengaduanProgres);
        txtStatusProgresPengaduan = findViewById(R.id.txtStatusProgresPengaduan);
        btnBatalkanPengaduan = findViewById(R.id.btnBatalkanPengaduan);
        btnBackProgresPengaduan = findViewById(R.id.btnBackProgresPengaduan);

        btnBackProgresPengaduan.setOnClickListener(v -> finish());

        // TODO: Load actual progress data based on user/submission ID
        // Using dummy data for now
        txtNamaPelaporProgres.setText("DirtyCat");
        txtJenisHewanPengaduanProgres.setText("Anjing");
        txtAlamatPengaduanProgres.setText("Surabaya");

        // --- SIMPAN STATUS AKTUAL DARI DATA YANG DILOAD ---
        // Contoh: Jika status dari database adalah "Diproses" atau "Diterima"
        // currentStatusPengaduan = "Diproses"; // Ganti ini dengan data asli
//        currentStatusPengaduan = "Diterima"; // Contoh jika statusnya Diterima
//        txtStatusProgresPengaduan.setText(currentStatusPengaduan);
        // txtStatusProgresPengaduan.setTextColor(getResources().getColor(R.color.status_diproses)); // Example color


        btnBatalkanPengaduan.setOnClickListener(v -> {
            // --- PENGECEKAN STATUS SEBELUM MEMBATALKAN ---
            if (currentStatusPengaduan.equalsIgnoreCase("Diterima")) {
                // Jika status "Diterima", tampilkan pop-up informasi
                showInfoDialog("Pengaduan yang sudah diterima/diproses tidak dapat dibatalkan.");
            } else {
                // Jika status BUKAN "Diterima" (misal: Diajukan, Menunggu Respon),
                // tampilkan pop-up konfirmasi pembatalan
                showConfirmationDialog(
                        "Konfirmasi Pembatalan",
                        "Apakah Anda yakin ingin membatalkan pengaduan ini?",
                        (dialog, which) -> {
                            // Aksi jika user menekan "Ya, Batalkan"
                            // TODO: Implement logic to cancel complaint submission in your data source (database/API)
                            Toast.makeText(ProgresPengaduanActivity.this, "Pengaduan dibatalkan!", Toast.LENGTH_SHORT).show();
                            // Optionally disable button or change status text after cancellation
                            btnBatalkanPengaduan.setEnabled(false);
                            btnBatalkanPengaduan.setText("Dibatalkan");
                            txtStatusProgresPengaduan.setText("Dibatalkan");
                            currentStatusPengaduan = "Dibatalkan"; // Update status lokal
                            // finish(); // Atau tutup activity jika perlu
                        }
                );
            }
        });
    }

    // Helper function to show an information dialog
    private void showInfoDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Informasi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info) // Optional icon
                .show();
    }

    // Helper function to show a confirmation dialog
    private void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener positiveAction) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ya, Batalkan", positiveAction) // Set positive action
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss()) // Just dismiss on negative
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional icon
                .show();
    }
}