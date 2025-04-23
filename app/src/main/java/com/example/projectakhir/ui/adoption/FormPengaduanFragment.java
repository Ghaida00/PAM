package com.example.projectakhir.ui.adoption;

import android.app.Activity; // Import Activity
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore; // Import MediaStore
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentFormPengaduanBinding; // Nama binding

public class FormPengaduanFragment extends Fragment {

    private FragmentFormPengaduanBinding binding; // View Binding
    private Uri selectedImageUri = null; // Untuk menyimpan URI gambar yang dipilih

    // Launcher untuk memilih gambar
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Tampilkan preview gambar yang dipilih
                    binding.imgPreview.setImageURI(selectedImageUri);
                    binding.imgPreview.setVisibility(View.VISIBLE);
                    binding.txtUploadHint.setText("Gambar Terpilih"); // Ubah teks hint
                } else {
                    // User membatalkan pemilihan atau terjadi error
                    Toast.makeText(requireContext(), "Pemilihan gambar dibatalkan", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormPengaduanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate FormPengaduanActivity ---

        // Setup tombol back
        binding.btnBackPengaduan.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup listener untuk layout upload gambar
        binding.layoutUploadGambar.setOnClickListener(v -> {
            // Buat intent untuk memilih gambar dari galeri
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Anda juga bisa menambahkan opsi untuk mengambil dari kamera jika diperlukan
            // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // intent.setType("image/*");
            imagePickerLauncher.launch(intent); // Luncurkan image picker
        });

        // Setup tombol konfirmasi pengaduan
        binding.btnKonfirmasiPengaduan.setOnClickListener(v -> {
            // Validasi input
            String namaPelapor = binding.inputNamaPelapor.getText().toString().trim();
            String jenisHewan = binding.inputJenisHewan.getText().toString().trim();
            String alamatLokasi = binding.inputAlamatLokasi.getText().toString().trim();
            String noHp = binding.inputNoHpPelapor.getText().toString().trim();
            String deskripsi = binding.inputDeskripsiHewan.getText().toString().trim();

            if (TextUtils.isEmpty(namaPelapor) || TextUtils.isEmpty(jenisHewan) ||
                    TextUtils.isEmpty(alamatLokasi) || TextUtils.isEmpty(noHp) ||
                    TextUtils.isEmpty(deskripsi)) {

                Toast.makeText(requireContext(), "Harap isi semua field teks", Toast.LENGTH_SHORT).show();
            } else if (selectedImageUri == null) {
                // Validasi apakah gambar sudah dipilih
                Toast.makeText(requireContext(), "Harap upload gambar keadaan hewan", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Implementasi logika pengiriman data pengaduan (termasuk selectedImageUri)
                // Kirim data ke ViewModel/Repository untuk diunggah/disimpan
                Toast.makeText(requireContext(), "Pengaduan berhasil dikirim!", Toast.LENGTH_LONG).show();

                // Kembali ke layar sebelumnya setelah berhasil
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        // --- Akhir kode dari onCreate FormPengaduanActivity ---
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
