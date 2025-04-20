package com.example.projectakhir;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface; // Import DialogInterface

public class ProgresAdopsiActivity extends AppCompatActivity {

    TextView txtNamaHewanProgres, txtJenisHewanProgres, txtKotaProgres, txtStatusProgresAdopsi;
    Button btnBatalkanAdopsi;
    ImageView btnBackProgresAdopsi;
    String currentStatusAdopsi = ""; // Variabel untuk menyimpan status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progres_adopsi);

        txtNamaHewanProgres = findViewById(R.id.txtNamaHewanProgres);
        txtJenisHewanProgres = findViewById(R.id.txtJenisHewanProgres);
        txtKotaProgres = findViewById(R.id.txtKotaProgres);
        txtStatusProgresAdopsi = findViewById(R.id.txtStatusProgresAdopsi);
        btnBatalkanAdopsi = findViewById(R.id.btnBatalkanAdopsi);
        btnBackProgresAdopsi = findViewById(R.id.btnBackProgresAdopsi);

        btnBackProgresAdopsi.setOnClickListener(v -> finish());

        // TODO: Load actual progress data based on user/submission ID
        // Using dummy data for now
        txtNamaHewanProgres.setText("Claire");
        txtJenisHewanProgres.setText("Anjing");
        txtKotaProgres.setText("Surabaya");

        // --- SIMPAN STATUS AKTUAL DARI DATA YANG DILOAD ---
        // Contoh: Jika status dari database adalah "Diterima"
        currentStatusAdopsi = "Diterima"; // Ganti ini dengan data asli
        txtStatusProgresAdopsi.setText(currentStatusAdopsi);
        // txtStatusProgresAdopsi.setTextColor(getResources().getColor(R.color.status_diterima)); // Example color


        btnBatalkanAdopsi.setOnClickListener(v -> {
            // --- PENGECEKAN STATUS SEBELUM MEMBATALKAN ---
            if (currentStatusAdopsi.equalsIgnoreCase("Diterima")) {
                // Jika status "Diterima", tampilkan pop-up informasi
                showInfoDialog("Pengajuan yang sudah diterima tidak dapat dibatalkan.");
            } else {
                // Jika status BUKAN "Diterima" (misal: Diproses, Diajukan, dll.),
                // tampilkan pop-up konfirmasi pembatalan
                showConfirmationDialog(
                        "Konfirmasi Pembatalan",
                        "Apakah Anda yakin ingin membatalkan pengajuan adopsi ini?",
                        (dialog, which) -> {
                            // Aksi jika user menekan "Ya, Batalkan"
                            // TODO: Implement logic to cancel adoption submission in your data source (database/API)
                            Toast.makeText(ProgresAdopsiActivity.this, "Pengajuan adopsi dibatalkan!", Toast.LENGTH_SHORT).show();
                            // Optionally disable button or change status text after cancellation
                            btnBatalkanAdopsi.setEnabled(false);
                            btnBatalkanAdopsi.setText("Dibatalkan");
                            txtStatusProgresAdopsi.setText("Dibatalkan");
                            currentStatusAdopsi = "Dibatalkan"; // Update status lokal
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