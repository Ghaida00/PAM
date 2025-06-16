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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Jika menggunakan ViewModel
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentAppointmentDetailBinding; // Nama binding
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AppointmentDetailFragment extends Fragment {

    private FragmentAppointmentDetailBinding binding; // View Binding
    private AppointmentDetailViewModel viewModel;
    private Uri selectedImageUri = null; // URI bukti pembayaran
    private String appointmentId; // ID appointment yang diterima

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imgUploadPreview.setImageURI(selectedImageUri);
                    binding.txtUploadHint.setText("Bukti Pembayaran Terpilih");
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
            // Tampilkan atau sembunyikan ProgressBar jika ada
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
                    NavHostFragment.findNavController(this).popBackStack();
                } else if ("confirmed_success".equals(result)) {
                    Toast.makeText(getContext(), "Appointment berhasil dikonfirmasi!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(this).popBackStack();
                }
                viewModel.clearUpdateResult();
            }
        });
    }

    private void displayAppointmentDetails(DocumentSnapshot doc) {
        if (doc == null) return;

        String serviceType = doc.getString("serviceType");
        List<String> layananList = (List<String>) doc.get("layananDipilih");
        String serviceDetails = (layananList != null) ? String.join(", ", layananList) : "";

        binding.txtServiceType.setText(serviceType);
        binding.txtServiceDetails.setText(serviceDetails);
        binding.txtProviderName.setText(doc.getString("providerName"));
        binding.txtProviderAddress.setText(doc.getString("providerAddress"));
        binding.txtAppointmentDateTime.setText(doc.getString("tanggalDipilih") + " - " + doc.getString("waktuDipilih"));

        String petType = doc.getString("petType");
        if (petType == null || petType.isEmpty()) {
            petType = "Hewan"; // Fallback
        }

        binding.txtPetNameType.setText(doc.getString("petName") + " - " + petType);

        if ("grooming".equalsIgnoreCase(serviceType)) {
            binding.imgServiceIcon.setImageResource(R.drawable.ic_spa);
        } else {
            binding.imgServiceIcon.setImageResource(R.drawable.ic_stethoscope);
        }
    }


    // Fungsi untuk setup semua listener
    private void setupListeners() {
        binding.btnBackAppointmentDetail.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.layoutUploadPayment.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnCancelAppointment.setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });

        binding.btnConfirmAppointment.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Harap unggah bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.confirmAppointmentWithProof(selectedImageUri);
        });
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Pembatalan")
                .setMessage("Apakah Anda yakin ingin membatalkan janji temu ini?")
                .setPositiveButton("Ya, Batalkan", (dialog, which) -> viewModel.cancelAppointment())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen detail appointment tidak ditemukan!", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
