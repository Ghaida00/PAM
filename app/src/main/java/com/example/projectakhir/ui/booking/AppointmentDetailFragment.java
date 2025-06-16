package com.example.projectakhir.ui.booking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentAppointmentDetailBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AppointmentDetailFragment extends Fragment {

    private FragmentAppointmentDetailBinding binding;
    private AppointmentDetailViewModel viewModel;
    private Uri selectedImageUri = null;
    private String appointmentId;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Gunakan Glide untuk memuat pratinjau
                    Glide.with(this)
                            .load(selectedImageUri)
                            .centerCrop()
                            .into(binding.imgUploadPreview);
                    binding.txtUploadHint.setText("Gambar Terpilih!");
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointmentId = AppointmentDetailFragmentArgs.fromBundle(getArguments()).getAppointmentId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AppointmentDetailViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
        observeViewModel();

        if (appointmentId != null) {
            viewModel.loadAppointmentDetails(appointmentId);
        } else {
            handleArgumentError();
        }
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.appointmentDetail.observe(getViewLifecycleOwner(), this::displayAppointmentDetails);

        viewModel.updateResult.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if ("cancelled_success".equals(result)) {
                    Toast.makeText(getContext(), "Appointment berhasil dibatalkan!", Toast.LENGTH_SHORT).show();
                    // Data akan di-refresh oleh ViewModel, tidak perlu popBackStack
                } else if ("confirmed_success".equals(result)) {
                    Toast.makeText(getContext(), "Bukti pembayaran berhasil diunggah!", Toast.LENGTH_LONG).show();
                } else if ("deleted_success".equals(result)) {
                    Toast.makeText(getContext(), "Riwayat appointment berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                }
                viewModel.clearUpdateResult();
            }
        });
    }

    private void displayAppointmentDetails(DocumentSnapshot doc) {
        if (doc == null || !isAdded()) return;

        String serviceType = doc.getString("serviceType");
        List<String> layananList = (List<String>) doc.get("layananDipilih");
        String serviceDetails = (layananList != null) ? String.join(", ", layananList) : "";
        String status = doc.getString("status");
        String paymentProofUrl = doc.getString("paymentProofUrl");

        binding.txtServiceType.setText(serviceType);
        binding.txtServiceDetails.setText(serviceDetails);
        binding.txtProviderName.setText(doc.getString("providerName"));
        binding.txtAppointmentDateTime.setText(doc.getString("tanggalDipilih") + " - " + doc.getString("waktuDipilih"));
        binding.txtPetNameType.setText(doc.getString("petName") + " - " + doc.getString("petType"));

        if ("grooming".equalsIgnoreCase(serviceType)) {
            binding.imgServiceIcon.setImageResource(R.drawable.ic_spa);
        } else {
            binding.imgServiceIcon.setImageResource(R.drawable.ic_stethoscope);
        }

        updateStatusUI(status, paymentProofUrl);
    }

    private void updateStatusUI(String status, String paymentProofUrl) {
        if (status == null || !isAdded()) return;

        binding.tvStatus.setText("Status: " + status);
        int statusColor;

        // Reset visibilitas tombol
        binding.buttonContainer.setVisibility(View.GONE);
        binding.btnDeleteAppointment.setVisibility(View.GONE);

        switch (status.toLowerCase()) {
            case "dikonfirmasi":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diterima);
                binding.tvPaymentProofLabel.setText("Bukti Pembayaran Terunggah");
                binding.layoutUploadPayment.setClickable(false);
                break;
            case "dibatalkan":
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_dibatalkan);
                binding.btnDeleteAppointment.setVisibility(View.VISIBLE); // Tampilkan tombol hapus
                binding.layoutUploadPayment.setClickable(false);
                break;
            case "menunggu konfirmasi":
            default:
                statusColor = ContextCompat.getColor(requireContext(), R.color.status_diproses);
                binding.buttonContainer.setVisibility(View.VISIBLE); // Tampilkan tombol batal & konfirmasi
                binding.layoutUploadPayment.setClickable(true);
                break;
        }

        binding.tvStatus.setTextColor(statusColor);

        // Periksa apakah ada URL bukti pembayaran dan tampilkan
        if (paymentProofUrl != null && !paymentProofUrl.isEmpty()) {
            binding.txtUploadHint.setText("Pembayaran telah dikonfirmasi");
            Glide.with(this)
                    .load(paymentProofUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_error_image)
                    .into(binding.imgUploadPreview);
        }
    }

    private void setupListeners() {
        binding.btnBackAppointmentDetail.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.layoutUploadPayment.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnCancelAppointment.setOnClickListener(v -> showCancelConfirmationDialog());

        binding.btnConfirmAppointment.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Harap unggah bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.confirmAppointmentWithProof(selectedImageUri);
        });

        // Listener untuk tombol hapus
        binding.btnDeleteAppointment.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Pembatalan")
                .setMessage("Apakah Anda yakin ingin membatalkan janji temu ini?")
                .setPositiveButton("Ya, Batalkan", (dialog, which) -> viewModel.cancelAppointment())
                .setNegativeButton("Tidak", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Dialog konfirmasi untuk hapus
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus riwayat janji temu ini secara permanen?")
                .setPositiveButton("Ya, Hapus", (dialog, which) -> viewModel.deleteAppointment())
                .setNegativeButton("Tidak", null)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen detail appointment tidak ditemukan!", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}