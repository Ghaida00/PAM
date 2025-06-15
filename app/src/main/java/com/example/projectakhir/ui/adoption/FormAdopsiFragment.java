package com.example.projectakhir.ui.adoption;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentFormAdopsiBinding;

public class FormAdopsiFragment extends Fragment {

    private FragmentFormAdopsiBinding binding;
    private FormAdopsiViewModel viewModel;

    // Variabel untuk menampung data dari argumen
    private String petId;
    private String petNama;
    private String petJenis;
    private String petKota;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                // Ambil semua argumen yang dikirim dari DetailHewanFragment
                petId = FormAdopsiFragmentArgs.fromBundle(getArguments()).getPetId();
                petNama = FormAdopsiFragmentArgs.fromBundle(getArguments()).getPetNama();
                petJenis = FormAdopsiFragmentArgs.fromBundle(getArguments()).getPetJenis();
                petKota = FormAdopsiFragmentArgs.fromBundle(getArguments()).getPetKota();
            } catch (Exception e) {
                handleArgumentError();
            }
        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Data hewan tidak lengkap!", Toast.LENGTH_SHORT).show();
        if (isAdded() && NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.formAdopsiFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormAdopsiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FormAdopsiViewModel.class);

        // Isi nama hewan dari argumen dan buat tidak bisa diedit
        if (petNama != null) {
            binding.inputNamaHewan.setText(petNama);
            binding.inputNamaHewan.setEnabled(false);
        }

        binding.btnBack.setOnClickListener(v -> {
            if (isAdded()) NavHostFragment.findNavController(this).popBackStack();
        });

        binding.btnKonfirmasi.setOnClickListener(v -> {
            // Validasi input dari pengguna
            String namaPemohon = binding.inputNamaPemohon.getText().toString().trim();
            String alamat = binding.inputAlamat.getText().toString().trim();
            String noHp = binding.inputNoHP.getText().toString().trim();
            String alasan = binding.inputAlasan.getText().toString().trim();

            if (TextUtils.isEmpty(namaPemohon) || TextUtils.isEmpty(alamat) ||
                    TextUtils.isEmpty(noHp) || TextUtils.isEmpty(alasan)) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
            } else {
                // Kirim semua data (termasuk dari argumen) ke ViewModel
                viewModel.submitAdoptionForm(petId, petNama, petJenis, petKota, namaPemohon, alamat, noHp, alasan);
            }
        });

        // Observe status dari ViewModel
        viewModel.isSubmitting.observe(getViewLifecycleOwner(), isSubmitting -> {
            binding.btnKonfirmasi.setEnabled(!isSubmitting);
            binding.progressBarFormAdopsi.setVisibility(isSubmitting ? View.VISIBLE : View.GONE);
        });

        viewModel.submissionStatus.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                if (status.equals("success")) {
                    Toast.makeText(requireContext(), "Pengajuan adopsi berhasil dikirim!", Toast.LENGTH_LONG).show();
                    if (isAdded()) NavHostFragment.findNavController(FormAdopsiFragment.this).popBackStack();
                } else if (!status.isEmpty()) { // Ada pesan error
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show();
                }
                viewModel.clearSubmissionStatus(); // Reset status agar tidak muncul lagi
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}