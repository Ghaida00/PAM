package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Import Toast
import android.net.Uri; // Import Uri
import androidx.activity.result.ActivityResultLauncher; // Import ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts; // Import ActivityResultContracts
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import android.content.DialogInterface; // Import DialogInterface


import androidx.appcompat.app.AppCompatActivity; // Import AppCompatActivity

public class AppointmentDetailActivity extends AppCompatActivity {

    // Deklarasi View
    ImageView btnBackAppointmentDetail, imgServiceIcon;
    TextView txtServiceType, txtServiceDetails, txtProviderName, txtProviderAddress,
            txtAppointmentDateTime, txtPetNameType, txtPetNotes, txtLocationHeader; // Tambahkan txtLocationHeader jika perlu
    LinearLayout layoutUploadPayment;
    Button btnCancelAppointment, btnConfirmAppointment;

    private Uri selectedImageUri = null; // Untuk menyimpan URI gambar
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ImageView imgUploadPreview; // Tambahkan ImageView di XML untuk preview jika mau
    private TextView txtUploadHint;     // TextView di dalam layout upload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail); // Set layout XML

        // Inisialisasi View
        btnBackAppointmentDetail = findViewById(R.id.btnBackAppointmentDetail);
        imgServiceIcon = findViewById(R.id.imgServiceIcon);
        txtServiceType = findViewById(R.id.txtServiceType);
        txtServiceDetails = findViewById(R.id.txtServiceDetails);
        txtProviderName = findViewById(R.id.txtProviderName);
        txtProviderAddress = findViewById(R.id.txtProviderAddress);
        txtAppointmentDateTime = findViewById(R.id.txtAppointmentDateTime);
        txtPetNameType = findViewById(R.id.txtPetNameType);
        txtPetNotes = findViewById(R.id.txtPetNotes);
        layoutUploadPayment = findViewById(R.id.layoutUploadPayment);
        btnCancelAppointment = findViewById(R.id.btnCancelAppointment);
        btnConfirmAppointment = findViewById(R.id.btnConfirmAppointment);
        imgUploadPreview = findViewById(R.id.imgUploadPreview); // Inisialisasi ImageView preview (buat ID ini di XML)
        txtUploadHint = findViewById(R.id.txtUploadHint); // Inisialisasi TextView hint (buat ID ini di XML)
        layoutUploadPayment = findViewById(R.id.layoutUploadPayment);
        // txtLocationHeader = findViewById(R.id.txtLocationHeader); // Inisialisasi jika header dipakai

        // Inisialisasi image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), // Kontrak untuk memilih konten (gambar)
                uri -> {
                    // Callback setelah gambar dipilih
                    if (uri != null) {
                        selectedImageUri = uri;
                        // Tampilkan preview (opsional)
                        // Contoh: ganti icon kamera dengan gambar yang dipilih
                        if (imgUploadPreview != null) {
                            imgUploadPreview.setImageURI(selectedImageUri);
                            imgUploadPreview.setVisibility(View.VISIBLE); // Tampilkan preview
                            if(txtUploadHint != null) txtUploadHint.setText("Gambar Terpilih"); // Ubah teks hint
                        } else {
                            // Atau cukup tampilkan pesan
                            Toast.makeText(this, "Gambar dipilih: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
                            if(txtUploadHint != null) txtUploadHint.setText("Bukti Pembayaran Terpilih");
                        }

                        // TODO: Mungkin perlu meng-upload URI ini nanti saat konfirmasi
                    } else {
                        // User tidak memilih gambar
                        Toast.makeText(this, "Pemilihan gambar dibatalkan", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ambil data dari Intent
        Intent intent = getIntent();
        String serviceType = intent.getStringExtra("serviceType"); // Ganti "serviceType" sesuai key yang kamu kirim
        String serviceDetails = intent.getStringExtra("serviceDetails");
        String providerName = intent.getStringExtra("providerName");
        String providerAddress = intent.getStringExtra("providerAddress");
        String appointmentDateTime = intent.getStringExtra("appointmentDateTime"); // Gabungan date & time
        String petNameType = intent.getStringExtra("petNameType");
        String petNotes = intent.getStringExtra("petNotes");
        int serviceIconRes = intent.getIntExtra("iconRes", R.drawable.ic_launcher_foreground); // Icon default
        // Ambil data lain jika ada (misal: status, ID appointment, dll.)

        // Set data ke View
        txtServiceType.setText(serviceType);
        txtServiceDetails.setText(serviceDetails);
        txtProviderName.setText(providerName);
        txtProviderAddress.setText(providerAddress);
        txtAppointmentDateTime.setText(appointmentDateTime);
        txtPetNameType.setText(petNameType);
        txtPetNotes.setText(petNotes);
        imgServiceIcon.setImageResource(serviceIconRes);
        // Set data lain jika ada

        // --- Setup Listener ---

        // Listener untuk Layout Upload Pembayaran
        layoutUploadPayment.setOnClickListener(v -> {
            // Luncurkan image picker
            imagePickerLauncher.launch("image/*"); // Filter hanya untuk tipe gambar
        });

        // Listener untuk Tombol Batal
        btnCancelAppointment.setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });

        // Tombol Back
        btnBackAppointmentDetail.setOnClickListener(v -> {
            finish(); // Menutup activity ini dan kembali ke activity sebelumnya
        });

        // Layout Upload Pembayaran
        layoutUploadPayment.setOnClickListener(v -> {
            // TODO: Implement logic untuk memilih gambar dari galeri atau kamera
            Toast.makeText(AppointmentDetailActivity.this, "Fitur upload belum ada", Toast.LENGTH_SHORT).show();
        });

        // Tombol Batal
        btnCancelAppointment.setOnClickListener(v -> {
            // TODO: Implement logic pembatalan (mungkin perlu konfirmasi dialog)
            // Contoh: tampilkan dialog konfirmasi, jika ya -> ubah status -> finish()
            Toast.makeText(AppointmentDetailActivity.this, "Tombol Batal ditekan", Toast.LENGTH_SHORT).show();
            // finish(); // Mungkin ditutup setelah batal
        });

        // Listener untuk Tombol Konfirmasi
        btnConfirmAppointment.setOnClickListener(v -> {
            // Validasi sederhana (contoh: cek apakah bukti bayar sudah diupload jika diperlukan)
            if (selectedImageUri == null) {
                // Ganti kondisi ini jika bukti bayar tidak selalu wajib
                Toast.makeText(AppointmentDetailActivity.this, "Harap upload bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                return; // Hentikan proses jika validasi gagal
            }

            // TODO: Implement logic untuk mengirim data ke server/database
            // Contoh:
            // String appointmentId = getIntent().getStringExtra("appointmentId"); // Ambil ID jika ada
            // uploadImageAndData(appointmentId, selectedImageUri, /* data lainnya */);

            // Setelah berhasil dikirim/diproses:
            // TODO: Implement logic untuk mengubah status appointment menjadi "Terkonfirmasi" atau status lain yang sesuai
            // Contoh: updateDatabaseStatus("Terkonfirmasi");

            Toast.makeText(AppointmentDetailActivity.this, "Appointment berhasil dikonfirmasi!", Toast.LENGTH_LONG).show();
            finish(); // Tutup activity setelah konfirmasi berhasil
        });
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pembatalan")
                .setMessage("Apakah Anda yakin ingin membatalkan appointment ini?")
                .setPositiveButton("Ya, Batalkan", (dialog, which) -> {
                    // Aksi jika user menekan "Ya, Batalkan"
                    // TODO: Implement logic untuk mengubah status appointment menjadi "Dibatalkan"
                    // Contoh: updateDatabaseStatus("Dibatalkan");

                    Toast.makeText(AppointmentDetailActivity.this, "Appointment dibatalkan!", Toast.LENGTH_SHORT).show();
                    finish(); // Tutup activity detail setelah dibatalkan
                })
                .setNegativeButton("Tidak", (dialog, which) -> {
                    // Aksi jika user menekan "Tidak"
                    dialog.dismiss(); // Tutup dialog saja
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Icon peringatan
                .show();
    }

    // TODO: Tambahkan onActivityResult untuk menangani hasil pemilihan gambar (jika implementasi upload)
}