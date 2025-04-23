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
import com.example.projectakhir.databinding.FragmentProgresPengaduanBinding; // Nama binding

public class ProgresPengaduanFragment extends Fragment {

    private FragmentProgresPengaduanBinding binding; // View Binding
    private String currentStatusPengaduan = ""; // Variabel untuk menyimpan status

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgresPengaduanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate ProgresPengaduanActivity ---

        // Setup tombol back
        binding.btnBackProgresPengaduan.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // TODO: Load actual progress data based on user/submission ID from ViewModel/Repository
        // Menggunakan data dummy untuk saat ini
        String namaPelapor = "DirtyCat";
        String jenisHewan = "Anjing";
        String alamat = "Surabaya";
        // Simulasikan status dari data yang di-load
        currentStatusPengaduan = "Diproses"; // Ganti ini dengan data asli ("Diterima", "Ditolak", dll.)

        // Tampilkan data dummy/aktual
        binding.txtNamaPelaporProgres.setText(namaPelapor);
        binding.txtJenisHewanPengaduanProgres.setText(jenisHewan);
        binding.txtAlamatPengaduanProgres.setText(alamat);
        updateStatusUI(currentStatusPengaduan); // Gunakan fungsi helper untuk update UI status


        // Setup tombol batalkan pengaduan
        binding.btnBatalkanPengaduan.setOnClickListener(v -> {
            // Pengecekan status sebelum membatalkan
            // Pengaduan biasanya bisa dibatalkan selama belum selesai/diterima sepenuhnya
            if (currentStatusPengaduan.equalsIgnoreCase("Selesai") || currentStatusPengaduan.equalsIgnoreCase("Dibatalkan")) { // Sesuaikan status akhir
                showInfoDialog("Pengaduan yang sudah " + currentStatusPengaduan.toLowerCase() + " tidak dapat dibatalkan lagi.");
            } else {
                // Jika status belum final, tampilkan konfirmasi
                showConfirmationDialog(
                        "Konfirmasi Pembatalan",
                        "Apakah Anda yakin ingin membatalkan pengaduan ini?",
                        (dialog, which) -> {
                            // Aksi jika user menekan "Ya, Batalkan"
                            // TODO: Implement logic to cancel complaint submission in ViewModel/Repository
                            Toast.makeText(requireContext(), "Pengaduan dibatalkan!", Toast.LENGTH_SHORT).show();

                            // Update UI setelah pembatalan
                            currentStatusPengaduan = "Dibatalkan";
                            updateStatusUI(currentStatusPengaduan);
                            binding.btnBatalkanPengaduan.setEnabled(false); // Disable tombol
                            // finish(); // Tidak perlu finish() di Fragment
                        }
                );
            }
        });

        // --- Akhir kode dari onCreate ProgresPengaduanActivity ---
    }

    // Fungsi helper untuk update UI status (termasuk warna dan teks)
    private void updateStatusUI(String status) {
        binding.txtStatusProgresPengaduan.setText(status);
        int statusColor;
        // Sesuaikan logika status dan warna sesuai kebutuhan alur pengaduan Anda
        switch (status.toLowerCase()) {
            case "diterima": // Atau "Selesai" atau status akhir positif lainnya
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diterima); // Hijau
                binding.btnBatalkanPengaduan.setEnabled(false);
                binding.btnBatalkanPengaduan.setAlpha(0.5f);
                break;
            case "diproses": // Atau "Menunggu Respon"
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diproses); // Oranye
                binding.btnBatalkanPengaduan.setEnabled(true);
                binding.btnBatalkanPengaduan.setAlpha(1.0f);
                break;
            case "ditolak": // Status akhir negatif
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_ditolak); // Merah
                binding.btnBatalkanPengaduan.setEnabled(false);
                binding.btnBatalkanPengaduan.setAlpha(0.5f);
                break;
            case "dibatalkan":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_dibatalkan); // Abu-abu
                binding.btnBatalkanPengaduan.setEnabled(false);
                binding.btnBatalkanPengaduan.setAlpha(0.5f);
                binding.btnBatalkanPengaduan.setText("Telah Dibatalkan");
                break;
            default: // Status awal seperti "Diajukan"
                statusColor = ContextCompat.getColor(requireContext(), R.color.default_text_color); // Warna default
                binding.btnBatalkanPengaduan.setEnabled(true); // Default bisa batal
                binding.btnBatalkanPengaduan.setAlpha(1.0f);
                break;
        }
        binding.txtStatusProgresPengaduan.setTextColor(statusColor);
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
