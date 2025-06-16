package com.example.projectakhir.ui.grooming;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity; // Import Gravity
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;
import android.widget.Toast; // Untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon; // Jika perlu model Salon (atau Vet)
import com.example.projectakhir.databinding.FragmentDetailVetBinding; // Nama binding

import java.util.ArrayList;
import java.util.List;

public class DetailVetFragment extends Fragment {

    private FragmentDetailVetBinding binding;
    private DetailVetViewModel viewModel;
    private String serviceId;
    private Salon currentVetData;
    private final ArrayList<String> layananDipilih = new ArrayList<>();
    private String jenisHewanDipilih = "Dog";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Mengambil ID unik dari argumen navigasi
            serviceId = DetailVetFragmentArgs.fromBundle(getArguments()).getServiceId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailVetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailVetViewModel.class);

        setupListeners();
        observeViewModel();

        // Meminta data detail dari ViewModel jika serviceId ada
        if (serviceId != null && !serviceId.isEmpty()) {
            viewModel.fetchVetDetails(serviceId);
        } else {
            Toast.makeText(getContext(), "ID Layanan tidak ditemukan.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.btnBook.setOnClickListener(v -> {
            if (currentVetData == null) {
                Toast.makeText(requireContext(), "Data klinik belum dimuat.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (layananDipilih.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih minimal satu layanan", Toast.LENGTH_SHORT).show();
            } else {
                navigateToBooking(currentVetData.getNama(), currentVetData.getId());
            }
        });
        setupJenisHewanSelection();
    }

    private void observeViewModel() {
        // Mengamati perubahan pada data detail vet
        viewModel.vetDetail.observe(getViewLifecycleOwner(), this::displayVetDetails);

        // Mengamati status loading
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.scrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        // Mengamati pesan error
        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
                NavHostFragment.findNavController(this).popBackStack(); // Kembali jika ada error
            }
        });
    }

    private void displayVetDetails(Salon vet) {
        if (vet == null) return;
        currentVetData = vet;

        binding.namaVet.setText(vet.getNama());
        binding.kotaVet.setText(vet.getKota());
        binding.descVet.setText("Klinik hewan terpercaya untuk kesehatan anak bulumu. Pemeriksaan dilakukan oleh dokter berpengalaman dengan penanganan penuh kasih. Buka: " + vet.getJam());

        // Memuat gambar dari URL
        if (vet.getImageUrl() != null && !vet.getImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(vet.getImageUrl())
                    .placeholder(R.drawable.anomali)
                    .error(R.drawable.ic_paw)
                    .into(binding.imgVet);
        }

        setupLayananTags(vet.getLayanan());
    }

    // Fungsi untuk setup pilihan jenis hewan (jika masih ada di desain/kebutuhan)
    private void setupJenisHewanSelection() {
        // Contoh sederhana menggunakan TextView yang bisa diklik
        View.OnClickListener jenisHewanListener = v -> {
            // Reset background semua pilihan
            binding.txtJenisDog.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisDog.setTextColor(Color.BLACK);
            binding.txtJenisCat.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisCat.setTextColor(Color.BLACK);
            binding.txtJenisOthers.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisOthers.setTextColor(Color.BLACK);

            // Set background yang dipilih
            v.setBackgroundResource(R.drawable.bg_tag_hijau); // Warna terpilih
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(Color.WHITE);
                jenisHewanDipilih = ((TextView) v).getText().toString();
            }
        };

        binding.txtJenisDog.setOnClickListener(jenisHewanListener);
        binding.txtJenisCat.setOnClickListener(jenisHewanListener);
        binding.txtJenisOthers.setOnClickListener(jenisHewanListener);

        // Set default selection (misal Dog)
        binding.txtJenisDog.performClick();
    }

    // Fungsi untuk membuat tag layanan (mirip DetailSalonFragment)
    private void setupLayananTags(List<String> layananTersedia) {
        binding.layananContainer.removeAllViews(); // Hapus view lama
        layananDipilih.clear(); // Reset pilihan
        if (layananTersedia == null) return;

        for (String l : layananTersedia) {
            TextView tag = new TextView(requireContext());
            tag.setText(l);
            tag.setTextSize(12f);
            tag.setPadding(24, 12, 24, 12);
            tag.setTextColor(Color.BLACK);
            tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Default: tidak dipilih
            tag.setGravity(Gravity.CENTER);
            tag.setClickable(true);
            tag.setFocusable(true);

            // Menggunakan LayoutParams dari FlexboxLayout jika parentnya FlexboxLayout
            com.google.android.flexbox.FlexboxLayout.LayoutParams lp =
                    new com.google.android.flexbox.FlexboxLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            // Margin antar tag
            lp.setMargins(0, 0, 16, 16); // Margin kanan dan bawah
            tag.setLayoutParams(lp);

            // Listener untuk memilih/batal memilih layanan
            tag.setOnClickListener(v -> {
                if (layananDipilih.contains(l)) {
                    layananDipilih.remove(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_kuning);
                    tag.setTextColor(Color.BLACK);
                } else {
                    layananDipilih.add(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_hijau);
                    tag.setTextColor(Color.WHITE);
                }
            });

            binding.layananContainer.addView(tag);
        }
    }

    // Fungsi untuk navigasi ke BookingFragment
    private void navigateToBooking(String providerName, String providerId) {
        try {
            DetailVetFragmentDirections.ActionDetailVetFragmentToBookingFragment action =
                    DetailVetFragmentDirections.actionDetailVetFragmentToBookingFragment(
                            providerName,
                            providerId,
                            currentVetData.getTipe(),
                            jenisHewanDipilih
                    );
            action.setLayananDipilih(layananDipilih.toArray(new String[0]));
            NavHostFragment.findNavController(this).navigate(action);
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Navigasi ke Booking belum siap.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
