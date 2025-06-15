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
import com.example.projectakhir.databinding.FragmentProgresAdopsiBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProgresAdopsiFragment extends Fragment {

    private FragmentProgresAdopsiBinding binding;
    private ProgresAdopsiViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgresAdopsiBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProgresAdopsiViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackProgresAdopsi.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        observeViewModel();

        binding.btnBatalkanAdopsi.setOnClickListener(v -> {
            // Mengambil status langsung dari LiveData di ViewModel
            String currentStatus = viewModel.status.getValue();
            if (currentStatus != null) {
                // Logika pembatalan tetap sama, karena status sudah di-handle di ViewModel
                if ("Diterima".equalsIgnoreCase(currentStatus) || "Ditolak".equalsIgnoreCase(currentStatus) || "Dibatalkan".equalsIgnoreCase(currentStatus)) {
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
            binding.btnBatalkanAdopsi.setEnabled(!isLoading);
            // Anda bisa menambahkan ProgressBar di layout dan mengaturnya di sini
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                if (error.equals("Belum ada pengajuan adopsi.")) {
                    binding.txtNamaHewanProgres.setText("-");
                    binding.txtJenisHewanProgres.setText("-");
                    binding.txtKotaProgres.setText("-");
                    binding.txtStatusProgresAdopsi.setText("Tidak Ada Pengajuan");
                    binding.txtStatusProgresAdopsi.setTextColor(ContextCompat.getColor(requireContext(), R.color.default_text_color));
                    binding.btnBatalkanAdopsi.setEnabled(false);
                }
                viewModel.clearError();
            }
        });

        // HANYA PERLU MENGAMATI SATU LIVEDATA
        viewModel.adoptionRequest.observe(getViewLifecycleOwner(), snapshot -> {
            if (snapshot != null && snapshot.exists()) {
                // Ambil semua data langsung dari satu snapshot
                binding.txtNamaHewanProgres.setText(snapshot.getString("namaHewan"));
                binding.txtJenisHewanProgres.setText(snapshot.getString("jenisHewan"));
                binding.txtKotaProgres.setText(snapshot.getString("kotaPengambilan"));
                updateStatusUI(snapshot.getString("status"));
            } else if (viewModel.error.getValue() == null) {
                // Handle jika snapshot null dan tidak ada pesan error spesifik
                binding.txtStatusProgresAdopsi.setText("Memuat...");
            }
        });
    }

    private void updateStatusUI(String status) {
        if (status == null) return;
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
            default: // "Diajukan"
                statusColor = ContextCompat.getColor(requireContext(), R.color.default_text_color);
                break;
        }
        binding.txtStatusProgresAdopsi.setTextColor(statusColor);
        binding.btnBatalkanAdopsi.setEnabled(canCancel);
        binding.btnBatalkanAdopsi.setText(buttonText);
        binding.btnBatalkanAdopsi.setAlpha(canCancel ? 1.0f : 0.5f);
    }

    private void showInfoDialog(String message) {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Informasi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener positiveAction) {
        if (!isAdded()) return;
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