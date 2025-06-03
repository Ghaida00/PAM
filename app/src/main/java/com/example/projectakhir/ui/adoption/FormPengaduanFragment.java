package com.example.projectakhir.ui.adoption;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
// Import ProgressBar jika ingin digunakan
// import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentFormPengaduanBinding;

public class FormPengaduanFragment extends Fragment {

    private FragmentFormPengaduanBinding binding;
    private Uri selectedImageUri = null;
    private FormPengaduanViewModel viewModel; // ViewModel

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imgPreview.setImageURI(selectedImageUri);
                    binding.imgPreview.setVisibility(View.VISIBLE);
                    binding.txtUploadHint.setText("Gambar Terpilih");
                } else {
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

        viewModel = new ViewModelProvider(this).get(FormPengaduanViewModel.class);

        binding.btnBackPengaduan.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.layoutUploadGambar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnKonfirmasiPengaduan.setOnClickListener(v -> {
            String namaPelapor = binding.inputNamaPelapor.getText().toString().trim();
            String jenisHewan = binding.inputJenisHewan.getText().toString().trim();
            String alamatLokasi = binding.inputAlamatLokasi.getText().toString().trim();
            String noHp = binding.inputNoHpPelapor.getText().toString().trim();
            String deskripsi = binding.inputDeskripsiHewan.getText().toString().trim();

            if (TextUtils.isEmpty(namaPelapor) || TextUtils.isEmpty(jenisHewan) ||
                    TextUtils.isEmpty(alamatLokasi) || TextUtils.isEmpty(noHp) ||
                    TextUtils.isEmpty(deskripsi)) {
                Toast.makeText(requireContext(), "Harap isi semua field teks", Toast.LENGTH_SHORT).show();
            } else if (selectedImageUri == null) { // Validasi gambar tetap penting
                Toast.makeText(requireContext(), "Harap upload gambar keadaan hewan", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.submitPengaduan(namaPelapor, jenisHewan, alamatLokasi, noHp, deskripsi, selectedImageUri);
            }
        });

        // Observe status dari ViewModel
        viewModel.isSubmitting.observe(getViewLifecycleOwner(), isSubmitting -> {
            if (isSubmitting) {
                binding.btnKonfirmasiPengaduan.setEnabled(false);
                // Tampilkan ProgressBar jika ada di layout XML
                // binding.progressBarFormPengaduan.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Mengirim pengaduan...", Toast.LENGTH_SHORT).show();
            } else {
                binding.btnKonfirmasiPengaduan.setEnabled(true);
                // Sembunyikan ProgressBar
                // binding.progressBarFormPengaduan.setVisibility(View.GONE);
            }
        });

        viewModel.submissionResult.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.equals("success")) {
                    Toast.makeText(requireContext(), "Pengaduan berhasil dikirim!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(FormPengaduanFragment.this).popBackStack();
                } else if (result.startsWith("error:")) {
                    Toast.makeText(requireContext(), result.substring("error: ".length()), Toast.LENGTH_LONG).show();
                }
                viewModel.clearSubmissionResult(); // Reset status
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}