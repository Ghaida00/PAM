package com.example.projectakhir.ui.adoption;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
// Import ProgressBar jika ingin menampilkan saat submit
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentFormAdopsiBinding;
// Hapus import AdoptionRepository jika menggunakan ViewModel
// import com.example.projectakhir.data.repository.AdoptionRepository;
// import com.google.firebase.auth.FirebaseAuth; // Pindahkan ke ViewModel jika ada

public class FormAdopsiFragment extends Fragment {

    private FragmentFormAdopsiBinding binding;
    private String namaHewanDiterima;
    private FormAdopsiViewModel viewModel; // ViewModel
    // private AdoptionRepository adoptionRepository; // Jika tidak pakai ViewModel

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                namaHewanDiterima = FormAdopsiFragmentArgs.fromBundle(getArguments()).getNamaHewan();
            } catch (IllegalArgumentException e) {
                namaHewanDiterima = getArguments().getString("namaHewan");
                if (namaHewanDiterima == null) {
                    handleArgumentError();
                }
            }
        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen nama hewan tidak ditemukan!", Toast.LENGTH_SHORT).show();
        if (getView() != null && NavHostFragment.findNavController(this).getCurrentDestination() != null &&
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
        // adoptionRepository = new AdoptionRepository(); // Jika tidak pakai ViewModel

        if (namaHewanDiterima != null) {
            binding.inputNamaHewan.setText(namaHewanDiterima);
            binding.inputNamaHewan.setEnabled(false);
        }

        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.btnKonfirmasi.setOnClickListener(v -> {
            // Validasi input
            String namaPemohon = binding.inputNamaPemohon.getText().toString().trim();
            String alamat = binding.inputAlamat.getText().toString().trim();
            String noHp = binding.inputNoHP.getText().toString().trim();
            String alasan = binding.inputAlasan.getText().toString().trim();

            if (TextUtils.isEmpty(namaPemohon) || TextUtils.isEmpty(alamat) ||
                    TextUtils.isEmpty(noHp) || TextUtils.isEmpty(alasan)) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
            } else {
                // Nonaktifkan tombol dan tampilkan progress bar jika ada
                binding.btnKonfirmasi.setEnabled(false);
                // binding.progressBarFormAdopsi.setVisibility(View.VISIBLE); // Jika ada ProgressBar

                viewModel.submitAdoptionForm(namaHewanDiterima, namaPemohon, alamat, noHp, alasan);

                // ---- Jika tidak menggunakan ViewModel, panggil repository langsung: ----
                // String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                //         FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";
                // adoptionRepository.submitAdoptionForm(namaHewanDiterima, namaPemohon, alamat, noHp, alasan, userId,
                //     new AdoptionRepository.FirestoreCallback<String>() {
                //         @Override
                //         public void onSuccess(String documentId) {
                //             binding.btnKonfirmasi.setEnabled(true);
                //             // binding.progressBarFormAdopsi.setVisibility(View.GONE);
                //             Toast.makeText(requireContext(), "Pengajuan adopsi berhasil dikirim!", Toast.LENGTH_LONG).show();
                //             NavHostFragment.findNavController(FormAdopsiFragment.this).popBackStack();
                //         }
                //         @Override
                //         public void onError(Exception e) {
                //             binding.btnKonfirmasi.setEnabled(true);
                //             // binding.progressBarFormAdopsi.setVisibility(View.GONE);
                //             Toast.makeText(requireContext(), "Gagal mengirim pengajuan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                //         }
                // });
            }
        });

        // Observe status dari ViewModel
        viewModel.isSubmitting.observe(getViewLifecycleOwner(), isSubmitting -> {
            if (isSubmitting) {
                binding.btnKonfirmasi.setEnabled(false);
                // Tampilkan ProgressBar jika ada di layout XML
                binding.progressBarFormAdopsi.setVisibility(View.VISIBLE);
            } else {
                binding.btnKonfirmasi.setEnabled(true);
                // Sembunyikan ProgressBar
                binding.progressBarFormAdopsi.setVisibility(View.GONE);
            }
        });

        viewModel.submissionStatus.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                if (status.equals("success")) {
                    Toast.makeText(requireContext(), "Pengajuan adopsi berhasil dikirim!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(FormAdopsiFragment.this).popBackStack();
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