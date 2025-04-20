package com.example.projectakhir;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FormPengaduanActivity extends AppCompatActivity {

    EditText inputNamaPelapor, inputJenisHewan, inputAlamatLokasi, inputNoHpPelapor, inputDeskripsiHewan;
    LinearLayout layoutUploadGambar;
    ImageView imgPreview, btnBackPengaduan;
    Button btnKonfirmasiPengaduan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pengaduan);

        inputNamaPelapor = findViewById(R.id.inputNamaPelapor);
        inputJenisHewan = findViewById(R.id.inputJenisHewan);
        inputAlamatLokasi = findViewById(R.id.inputAlamatLokasi);
        inputNoHpPelapor = findViewById(R.id.inputNoHpPelapor);
        inputDeskripsiHewan = findViewById(R.id.inputDeskripsiHewan);
        layoutUploadGambar = findViewById(R.id.layoutUploadGambar);
        imgPreview = findViewById(R.id.imgPreview);
        btnKonfirmasiPengaduan = findViewById(R.id.btnKonfirmasiPengaduan);
        btnBackPengaduan = findViewById(R.id.btnBackPengaduan);

        btnBackPengaduan.setOnClickListener(v -> finish());

        // TODO: Implement image picking logic for layoutUploadGambar onClick
        layoutUploadGambar.setOnClickListener(v -> {
            // Intent to pick image from gallery or camera
            Toast.makeText(FormPengaduanActivity.this, "Fitur upload gambar belum diimplementasikan", Toast.LENGTH_SHORT).show();
        });

        btnKonfirmasiPengaduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Basic Validation
                if (inputNamaPelapor.getText().toString().trim().isEmpty() ||
                        inputJenisHewan.getText().toString().trim().isEmpty() ||
                        inputAlamatLokasi.getText().toString().trim().isEmpty() ||
                        inputNoHpPelapor.getText().toString().trim().isEmpty() ||
                        inputDeskripsiHewan.getText().toString().trim().isEmpty()) {

                    Toast.makeText(FormPengaduanActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: Send data to server or save locally
                    Toast.makeText(FormPengaduanActivity.this, "Pengaduan berhasil dikirim!", Toast.LENGTH_LONG).show();
                    finish(); // Close activity after submission
                }
            }
        });
    }

    // TODO: Add onActivityResult to handle picked image and set to imgPreview
}