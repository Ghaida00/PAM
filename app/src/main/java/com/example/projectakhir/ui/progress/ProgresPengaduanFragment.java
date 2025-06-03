// In main/java/com/example/projectakhir/ui/progress/ProgresPengaduanFragment.java
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProgresPengaduanBinding;
import com.google.firebase.firestore.DocumentSnapshot; // Import

public class ProgresPengaduanFragment extends Fragment {

    private FragmentProgresPengaduanBinding binding;
    private ProgresPengaduanViewModel viewModel;
    // Hapus: private String currentStatusPengaduan = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgresPengaduanBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProgresPengaduanViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackProgresPengaduan.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        observeViewModel();

        binding.btnBatalkanPengaduan.setOnClickListener(v -> {
            String currentStatus = viewModel.status.getValue();
            if (currentStatus != null) {
                // Sesuaikan status akhir jika berbeda untuk pengaduan
                if ("Selesai".equalsIgnoreCase(currentStatus) || "Dibatalkan".equalsIgnoreCase(currentStatus)) {
                    showInfoDialog("Pengaduan yang sudah " + currentStatus.toLowerCase() + " tidak dapat dibatalkan lagi.");
                } else {
                    showConfirmationDialog(
                            "Konfirmasi Pembatalan",
                            "Apakah Anda yakin ingin membatalkan pengaduan ini?",
                            (dialog, which) -> viewModel.cancelReport()
                    );
                }
            } else {
                Toast.makeText(requireContext(), "Status pengaduan belum dimuat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnBatalkanPengaduan.setEnabled(!isLoading);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                if (error.equals("Belum ada pengaduan.")) {
                    binding.txtNamaPelaporProgres.setText("-");
                    binding.txtJenisHewanPengaduanProgres.setText("-");
                    binding.txtAlamatPengaduanProgres.setText("-");
                    binding.txtStatusProgresPengaduan.setText("Tidak Ada Pengaduan");
                    binding.txtStatusProgresPengaduan.setTextColor(ContextCompat.getColor(requireContext(), R.color.default_text_color));
                    binding.btnBatalkanPengaduan.setEnabled(false);
                }
                viewModel.clearError();
            }
        });

        viewModel.userReport.observe(getViewLifecycleOwner(), snapshot -> {
            if (snapshot != null && snapshot.exists()) {
                binding.txtNamaPelaporProgres.setText(snapshot.getString("namaPelapor"));
                binding.txtJenisHewanPengaduanProgres.setText(snapshot.getString("jenisHewan"));
                binding.txtAlamatPengaduanProgres.setText(snapshot.getString("alamatLokasi"));
                // Status di-handle oleh observer status sendiri
            } else if (viewModel.error.getValue() == null || !viewModel.error.getValue().equals("Belum ada pengaduan.")){
                // Handle jika snapshot null tapi bukan karena "Belum ada pengaduan"
                // binding.txtNamaPelaporProgres.setText("-");
                // binding.txtJenisHewanPengaduanProgres.setText("-");
                // binding.txtAlamatPengaduanProgres.setText("-");
                // binding.txtStatusProgresPengaduan.setText("Gagal memuat");
                // binding.btnBatalkanPengaduan.setEnabled(false);
            }
        });

        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                updateStatusUI(status);
            } else if (viewModel.error.getValue() == null || !viewModel.error.getValue().equals("Belum ada pengaduan.")) {
                // binding.txtStatusProgresPengaduan.setText("Memuat...");
            }
        });
    }

    private void updateStatusUI(String status) {
        binding.txtStatusProgresPengaduan.setText(status);
        int statusColor;
        boolean canCancel = true;
        String buttonText = "Batalkan Pengaduan";

        switch (status.toLowerCase()) {
            case "diterima": // Atau "Selesai" untuk pengaduan
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diterima);
                canCancel = false;
                break;
            case "selesai": // Status akhir positif untuk pengaduan
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
        binding.txtStatusProgresPengaduan.setTextColor(statusColor);
        binding.btnBatalkanPengaduan.setEnabled(canCancel);
        binding.btnBatalkanPengaduan.setText(buttonText);
        binding.btnBatalkanPengaduan.setAlpha(canCancel ? 1.0f : 0.5f);
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