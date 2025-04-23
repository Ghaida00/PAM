package com.example.projectakhir.ui.adoption;

import android.os.Bundle;
import android.text.TextUtils; // Import TextUtils for checking empty strings
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentFormAdopsiBinding; // Nama binding

public class FormAdopsiFragment extends Fragment {

    private FragmentFormAdopsiBinding binding; // View Binding
    private String namaHewanDiterima;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen "namaHewan"
        if (getArguments() != null) {
            try {
                // Menggunakan Safe Args
                namaHewanDiterima = FormAdopsiFragmentArgs.fromBundle(getArguments()).getNamaHewan();
            } catch (IllegalArgumentException e) {
                // Fallback jika Safe Args belum siap
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
        // Navigasi kembali jika argumen tidak valid
        if (NavHostFragment.findNavController(this).getCurrentDestination() != null &&
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

        // --- Kode dari onCreate FormAdopsiActivity ---

        // Set nama hewan yang diterima dari argumen
        if (namaHewanDiterima != null) {
            binding.inputNamaHewan.setText(namaHewanDiterima);
            binding.inputNamaHewan.setEnabled(false); // Tetap disable field nama hewan
        }

        // Setup tombol back (jika tidak pakai Toolbar AppActivity)
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup tombol konfirmasi
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
                // TODO: Implementasi logika pengiriman data adopsi (ke ViewModel/Repository)
                Toast.makeText(requireContext(), "Pengajuan adopsi berhasil dikirim!", Toast.LENGTH_LONG).show();

                // Kembali ke layar sebelumnya (Detail Hewan) setelah berhasil
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        // --- Akhir kode dari onCreate FormAdopsiActivity ---
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
