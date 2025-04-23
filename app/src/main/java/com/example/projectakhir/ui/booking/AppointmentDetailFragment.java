package com.example.projectakhir.ui.booking;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.example.projectakhir.data.AppointmentData; // Jika perlu model
import com.example.projectakhir.databinding.FragmentAppointmentDetailBinding; // Nama binding
// Import ViewModel jika ada (misal: AppointmentDetailViewModel)

public class AppointmentDetailFragment extends Fragment {

    private FragmentAppointmentDetailBinding binding; // View Binding
    private Uri selectedImageUri = null; // URI bukti pembayaran
    private String appointmentId; // ID appointment yang diterima

    // Launcher untuk image picker
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imgUploadPreview.setImageURI(selectedImageUri);
                    // binding.imgUploadPreview.setVisibility(View.VISIBLE); // Pastikan preview terlihat
                    binding.txtUploadHint.setText("Bukti Pembayaran Terpilih");
                } else {
                    Toast.makeText(requireContext(), "Pemilihan gambar dibatalkan", Toast.LENGTH_SHORT).show();
                }
            });

    // ViewModel (opsional, tapi direkomendasikan untuk mengambil data detail)
    // private AppointmentDetailViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen appointmentId
        if (getArguments() != null) {
            try {
                appointmentId = AppointmentDetailFragmentArgs.fromBundle(getArguments()).getAppointmentId();
            } catch (IllegalArgumentException e) {
                appointmentId = getArguments().getString("appointmentId"); // Fallback
                if (appointmentId == null) {
                    handleArgumentError();
                }
            }
            // TODO: Minta detail appointment dari ViewModel berdasarkan appointmentId
            // viewModel.loadAppointmentDetails(appointmentId);
        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen detail appointment tidak ditemukan!", Toast.LENGTH_SHORT).show();
        if (NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.appointmentDetailFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi ViewModel (jika ada)
        // viewModel = new ViewModelProvider(this).get(AppointmentDetailViewModel.class);

        // Observe LiveData dari ViewModel untuk menampilkan detail (jika pakai ViewModel)
        // viewModel.getAppointmentDetails().observe(getViewLifecycleOwner(), appointmentData -> {
        //     if (appointmentData != null) {
        //         displayAppointmentDetails(appointmentData);
        //     } else {
        //         // Handle jika data tidak ditemukan
        //         Toast.makeText(requireContext(), "Detail appointment tidak ditemukan", Toast.LENGTH_SHORT).show();
        //         NavHostFragment.findNavController(this).popBackStack();
        //     }
        // });

        // --- Tampilkan Data Dummy/Placeholder (Hapus jika sudah pakai ViewModel) ---
        displayDummyDetails();
        // --- Akhir Data Dummy ---

        setupListeners();
    }

    // Fungsi untuk menampilkan detail (dipanggil setelah data didapat dari ViewModel)
    private void displayAppointmentDetails(AppointmentData data) {
        binding.txtServiceType.setText(data.getServiceType());
        binding.txtServiceDetails.setText(data.getServiceDetails());
        binding.txtProviderName.setText(data.getProviderName());
        binding.txtProviderAddress.setText(data.getProviderAddress());
        binding.txtAppointmentDateTime.setText(data.getAppointmentDay() + " - " + data.getAppointmentTime());
        binding.txtPetNameType.setText(data.getPetName() + " - " + data.getPetType());
        binding.txtPetNotes.setText(data.getPetNotes());
        binding.imgServiceIcon.setImageResource(data.getIconRes());
        // Set data lain jika ada (misal: status, dll.)
    }

    // Fungsi untuk menampilkan data dummy (HAPUS JIKA SUDAH PAKAI VIEWMODEL)
    private void displayDummyDetails() {
        binding.txtServiceType.setText("Grooming (Dummy)");
        binding.txtServiceDetails.setText("Spa - 1 Hour Course (Dummy)");
        binding.txtProviderName.setText("Barber Pet (Dummy)");
        binding.txtProviderAddress.setText("Jl. Dr. Ir. H. Soekarno No.178, Mulyorejo, Kec. Mulyorejo, Surabaya (Dummy)");
        binding.txtAppointmentDateTime.setText("Today - 15:00 (Dummy)");
        binding.txtPetNameType.setText("Grace - Kucing (Dummy)");
        binding.txtPetNotes.setText("Note: Nakal Jahil dan Gasuka dipegang perutnya (Dummy)");
        binding.imgServiceIcon.setImageResource(R.drawable.ic_spa); // Icon dummy
        binding.txtLocationHeader.setText("Surabaya"); // Contoh lokasi header
    }


    // Fungsi untuk setup semua listener
    private void setupListeners() {
        // Tombol Back
        binding.btnBackAppointmentDetail.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Layout Upload Pembayaran
        binding.layoutUploadPayment.setOnClickListener(v -> {
            // Luncurkan image picker
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Tombol Batal
        binding.btnCancelAppointment.setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });

        // Tombol Konfirmasi
        binding.btnConfirmAppointment.setOnClickListener(v -> {
            // Validasi sederhana (cek apakah bukti bayar sudah diupload jika diperlukan)
            // TODO: Sesuaikan validasi ini berdasarkan status appointment
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Harap upload bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                return; // Hentikan proses jika validasi gagal
            }

            // TODO: Implement logic untuk mengirim konfirmasi ke ViewModel/Repository
            // Kirim appointmentId dan selectedImageUri
            // viewModel.confirmAppointment(appointmentId, selectedImageUri);

            Toast.makeText(requireContext(), "Appointment berhasil dikonfirmasi!", Toast.LENGTH_LONG).show();
            // Kembali ke layar sebelumnya (HeartFragment) setelah konfirmasi
            NavHostFragment.findNavController(this).popBackStack(R.id.heartFragment, false);
        });
    }


    // Fungsi untuk menampilkan dialog konfirmasi batal (dipindahkan ke sini)
    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(requireContext()) // Gunakan requireContext()
                .setTitle("Konfirmasi Pembatalan")
                .setMessage("Apakah Anda yakin ingin membatalkan appointment ini?")
                .setPositiveButton("Ya, Batalkan", (dialog, which) -> {
                    // Aksi jika user menekan "Ya, Batalkan"
                    // TODO: Implement logic untuk membatalkan appointment di ViewModel/Repository
                    // viewModel.cancelAppointment(appointmentId);

                    Toast.makeText(requireContext(), "Appointment dibatalkan!", Toast.LENGTH_SHORT).show();
                    // Kembali ke layar sebelumnya (HeartFragment) setelah dibatalkan
                    NavHostFragment.findNavController(this).popBackStack(R.id.heartFragment, false);
                })
                .setNegativeButton("Tidak", (dialog, which) -> {
                    // Aksi jika user menekan "Tidak"
                    dialog.dismiss(); // Tutup dialog saja
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Icon peringatan
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
