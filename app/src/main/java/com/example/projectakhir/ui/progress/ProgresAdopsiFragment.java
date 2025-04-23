package com.example.projectakhir.ui.progress;

import android.content.DialogInterface; // Import DialogInterface
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import androidx.core.content.ContextCompat; // Import ContextCompat for colors
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProgresAdopsiBinding; // Nama binding

public class ProgresAdopsiFragment extends Fragment {

    private FragmentProgresAdopsiBinding binding; // View Binding
    private String currentStatusAdopsi = ""; // Variabel untuk menyimpan status

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgresAdopsiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate ProgresAdopsiActivity ---

        // Setup tombol back
        binding.btnBackProgresAdopsi.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // TODO: Load actual progress data based on user/submission ID from ViewModel/Repository
        // Menggunakan data dummy untuk saat ini
        String namaHewan = "Claire";
        String jenisHewan = "Anjing";
        String kota = "Surabaya";
        // Simulasikan status dari data yang di-load
        currentStatusAdopsi = "Diterima"; // Ganti ini dengan data asli ("Diproses", "Ditolak", dll.)

        // Tampilkan data dummy/aktual
        binding.txtNamaHewanProgres.setText(namaHewan);
        binding.txtJenisHewanProgres.setText(jenisHewan);
        binding.txtKotaProgres.setText(kota);
        updateStatusUI(currentStatusAdopsi); // Gunakan fungsi helper untuk update UI status


        // Setup tombol batalkan adopsi
        binding.btnBatalkanAdopsi.setOnClickListener(v -> {
            // Pengecekan status sebelum membatalkan
            if (currentStatusAdopsi.equalsIgnoreCase("Diterima") || currentStatusAdopsi.equalsIgnoreCase("Dibatalkan")) {
                // Jika status "Diterima" atau sudah "Dibatalkan", tampilkan info
                showInfoDialog("Pengajuan yang sudah " + currentStatusAdopsi.toLowerCase() + " tidak dapat dibatalkan lagi.");
            } else {
                // Jika status BUKAN "Diterima" (misal: Diproses, Diajukan), tampilkan konfirmasi
                showConfirmationDialog(
                        "Konfirmasi Pembatalan",
                        "Apakah Anda yakin ingin membatalkan pengajuan adopsi ini?",
                        (dialog, which) -> {
                            // Aksi jika user menekan "Ya, Batalkan"
                            // TODO: Implement logic to cancel adoption submission in ViewModel/Repository
                            Toast.makeText(requireContext(), "Pengajuan adopsi dibatalkan!", Toast.LENGTH_SHORT).show();

                            // Update UI setelah pembatalan
                            currentStatusAdopsi = "Dibatalkan";
                            updateStatusUI(currentStatusAdopsi);
                            binding.btnBatalkanAdopsi.setEnabled(false); // Disable tombol setelah dibatalkan
                            // finish(); // Tidak perlu finish() di Fragment, cukup update UI
                        }
                );
            }
        });

        // --- Akhir kode dari onCreate ProgresAdopsiActivity ---
    }

    // Fungsi helper untuk update UI status (termasuk warna dan teks)
    private void updateStatusUI(String status) {
        binding.txtStatusProgresAdopsi.setText(status);
        int statusColor;
        switch (status.toLowerCase()) {
            case "diterima":
                // Ganti dengan warna hijau yang sesuai dari colors.xml jika ada
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diterima); // Contoh: Buat color resource
                binding.btnBatalkanAdopsi.setEnabled(false); // Tidak bisa batal jika diterima
                binding.btnBatalkanAdopsi.setAlpha(0.5f); // Buat tombol terlihat disabled
                break;
            case "diproses":
                // Ganti dengan warna kuning/oranye yang sesuai
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diproses); // Contoh
                binding.btnBatalkanAdopsi.setEnabled(true);
                binding.btnBatalkanAdopsi.setAlpha(1.0f);
                break;
            case "ditolak":
                // Ganti dengan warna merah yang sesuai
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_ditolak); // Contoh
                binding.btnBatalkanAdopsi.setEnabled(false);
                binding.btnBatalkanAdopsi.setAlpha(0.5f);
                break;
            case "dibatalkan":
                // Ganti dengan warna abu-abu yang sesuai
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_dibatalkan); // Contoh
                binding.btnBatalkanAdopsi.setEnabled(false);
                binding.btnBatalkanAdopsi.setAlpha(0.5f);
                binding.btnBatalkanAdopsi.setText("Telah Dibatalkan"); // Ubah teks tombol
                break;
            default:
                statusColor = ContextCompat.getColor(requireContext(), R.color.default_text_color); // Warna default
                binding.btnBatalkanAdopsi.setEnabled(true); // Default bisa batal
                binding.btnBatalkanAdopsi.setAlpha(1.0f);
                break;
        }
        binding.txtStatusProgresAdopsi.setTextColor(statusColor);
    }


    // Helper function to show an information dialog (dipindahkan ke sini)
    private void showInfoDialog(String message) {
        // Gunakan requireContext() untuk Builder
        new AlertDialog.Builder(requireContext())
                .setTitle("Informasi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info) // Optional icon
                .show();
    }

    // Helper function to show a confirmation dialog (dipindahkan ke sini)
    private void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener positiveAction) {
        // Gunakan requireContext() untuk Builder
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ya, Batalkan", positiveAction) // Set positive action
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss()) // Just dismiss on negative
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional icon
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
