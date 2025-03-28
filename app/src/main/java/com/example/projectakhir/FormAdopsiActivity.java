package com.example.projectakhir;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FormAdopsiActivity extends AppCompatActivity {

    EditText inputNamaHewan, inputNamaPemohon, inputAlamat, inputNoHP, inputAlasan;
    Button btnKonfirmasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_adopsi);

        inputNamaHewan = findViewById(R.id.inputNamaHewan);
        inputNamaPemohon = findViewById(R.id.inputNamaPemohon);
        inputAlamat = findViewById(R.id.inputAlamat);
        inputNoHP = findViewById(R.id.inputNoHP);
        inputAlasan = findViewById(R.id.inputAlasan);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);

        String namaHewan = getIntent().getStringExtra("namaHewan");
        inputNamaHewan.setText(namaHewan);
        inputNamaHewan.setEnabled(false);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputNamaPemohon.getText().toString().isEmpty() ||
                        inputAlamat.getText().toString().isEmpty() ||
                        inputNoHP.getText().toString().isEmpty() ||
                        inputAlasan.getText().toString().isEmpty()) {
                    Toast.makeText(FormAdopsiActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FormAdopsiActivity.this, "Pengajuan adopsi berhasil dikirim!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
