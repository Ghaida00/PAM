// In main/java/com/example/projectakhir/ui/progress/ProgresAdopsiFragment.java
package com.example.projectakhir.ui.progress;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider
import androidx.navigation.fragment.NavHostFragment;
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProgresAdopsiBinding;
import com.google.firebase.firestore.DocumentSnapshot; // Import DocumentSnapshot

public class ProgresAdopsiFragment extends Fragment {

    private FragmentProgresAdopsiBinding binding;
    private ProgresAdopsiViewModel viewModel; // ViewModel
    // Hapus: private String currentStatusAdopsi = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgresAdopsiBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProgresAdopsiViewModel.class); // Inisialisasi ViewModel
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackProgresAdopsi.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        observeViewModel();

        binding.btnBatalkanAdopsi.setOnClickListener(v -> {
            String currentStatus = viewModel.status.getValue();
            if (currentStatus != null) {
                if ("Diterima".equalsIgnoreCase(currentStatus) || "Dibatalkan".equalsIgnoreCase(currentStatus)) {
                    showInfoDialog("Pengajuan yang sudah " + currentStatus.toLowerCase() + " tidak dapat dibatalkan lagi.");
                } else {
                    showConfirmationDialog(
                            "Konfirmasi Pembatalan",
                            "Apakah Anda yakin ingin membatalkan pengajuan adopsi ini?",
                            (dialog, which) -> viewModel.cancelAdoption()
                    );
                }
            } else {
                Toast.makeText(requireContext(), "Status pengajuan belum dimuat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            // Tampilkan/sembunyikan ProgressBar jika ada di layout
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnBatalkanAdopsi.setEnabled(!isLoading); // Nonaktifkan tombol saat loading
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                // Kosongkan field jika ada error signifikan (misal, tidak ada data)
                if (error.equals("Belum ada pengajuan adopsi.")) {
                    binding.txtNamaHewanProgres.setText("-");
                    binding.txtJenisHewanProgres.setText("-");
                    binding.txtKotaProgres.setText("-");
                    binding.txtStatusProgresAdopsi.setText("Tidak Ada Pengajuan");
                    binding.txtStatusProgresAdopsi.setTextColor(ContextCompat.getColor(requireContext(), R.color.default_text_color));
                    binding.btnBatalkanAdopsi.setEnabled(false);
                }
                viewModel.clearError(); // Bersihkan error setelah ditampilkan
            }
        });

        // Observe nama hewan dari ViewModel
        viewModel.hewanNama.observe(getViewLifecycleOwner(), nama -> {
            binding.txtNamaHewanProgres.setText(nama != null ? nama : "-");
        });

        // Observe jenis hewan dari ViewModel
        viewModel.hewanJenis.observe(getViewLifecycleOwner(), jenis -> {
            binding.txtJenisHewanProgres.setText(jenis != null ? jenis : "-");
        });

        // Observe kota hewan dari ViewModel
        viewModel.hewanKota.observe(getViewLifecycleOwner(), kota -> {
            binding.txtKotaProgres.setText(kota != null ? kota : "-");
        });


        viewModel.adoptionRequest.observe(getViewLifecycleOwner(), snapshot -> {
            if (snapshot != null && snapshot.exists()) {
                // Data sudah di-set oleh observer status, nama, jenis, kota
            } else if (viewModel.error.getValue() == null || !viewModel.error.getValue().equals("Belum ada pengajuan adopsi.")){
                // Handle jika snapshot null tapi bukan karena "Belum ada pengajuan"
                // Ini bisa terjadi jika fetch awal gagal karena alasan lain
                // binding.txtNamaHewanProgres.setText("-");
                // binding.txtJenisHewanProgres.setText("-");
                // binding.txtKotaProgres.setText("-");
                // binding.txtStatusProgresAdopsi.setText("Gagal memuat");
                // binding.btnBatalkanAdopsi.setEnabled(false);
            }
        });

        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                updateStatusUI(status);
            } else if (viewModel.error.getValue() == null || !viewModel.error.getValue().equals("Belum ada pengajuan adopsi.")){
                // Jika status null tapi tidak ada error spesifik "belum ada pengajuan"
                // Bisa jadi ini adalah state awal sebelum data di-load atau ada error lain
                // binding.txtStatusProgresAdopsi.setText("Memuat...");
            }
        });
    }

    private void updateStatusUI(String status) {
        binding.txtStatusProgresAdopsi.setText(status);
        int statusColor;
        boolean canCancel = true;
        String buttonText = "Batalkan Pengajuan";

        switch (status.toLowerCase()) {
            case "diterima":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diterima);
                canCancel = false;
                break;
            case "diproses":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diproses);
                break;
            case "ditolak":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_ditolak);
                canCancel = false;
                break;
            case "dibatalkan":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_dibatalkan);
                canCancel = false;
                buttonText = "Telah Dibatalkan";
                break;
            default: // "Diajukan" atau status lain
                statusColor = ContextCompat.getColor(requireContext(), R.color.default_text_color);
                break;
        }
        binding.txtStatusProgresAdopsi.setTextColor(statusColor);
        binding.btnBatalkanAdopsi.setEnabled(canCancel);
        binding.btnBatalkanAdopsi.setText(buttonText);
        binding.btnBatalkanAdopsi.setAlpha(canCancel ? 1.0f : 0.5f);
    }

    private void showInfoDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Informasi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener positiveAction) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ya, Batalkan", positiveAction)
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}