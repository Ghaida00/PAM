package com.example.projectakhir.ui.grooming;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon; // Jika perlu model Salon
import com.example.projectakhir.databinding.FragmentSalonDetailBinding; // Nama binding

import java.util.ArrayList;
import java.util.List;

public class DetailSalonFragment extends Fragment {

    private FragmentSalonDetailBinding binding; // View Binding
    private DetailSalonViewModel viewModel;
    private String serviceId;
    private Salon currentSalonData;
    private final ArrayList<String> layananDipilih = new ArrayList<>();
    private String jenisHewanDipilih = "Dog";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceId = DetailSalonFragmentArgs.fromBundle(getArguments()).getServiceId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSalonDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailSalonViewModel.class);

        setupListeners();
        observeViewModel();

        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        if (serviceId != null && !serviceId.isEmpty()) {
            viewModel.fetchSalonDetails(serviceId);
        } else {
            Toast.makeText(getContext(), "ID Layanan tidak ditemukan.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.btnBook.setOnClickListener(v -> {
            if (currentSalonData == null) {
                Toast.makeText(requireContext(), "Data salon belum dimuat.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (layananDipilih.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih minimal satu layanan", Toast.LENGTH_SHORT).show();
            } else {
                navigateToBooking(currentSalonData.getNama(), currentSalonData.getId());
            }
        });

        setupJenisHewanSelection();
    }

    private void setupJenisHewanSelection() {
        View.OnClickListener jenisHewanListener = v -> {
            binding.txtJenisDog.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisDog.setTextColor(Color.BLACK);
            binding.txtJenisCat.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisCat.setTextColor(Color.BLACK);
            binding.txtJenisOthers.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisOthers.setTextColor(Color.BLACK);

            v.setBackgroundResource(R.drawable.bg_tag_hijau);
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(Color.WHITE);
                jenisHewanDipilih = ((TextView) v).getText().toString();
            }
        };

        binding.txtJenisDog.setOnClickListener(jenisHewanListener);
        binding.txtJenisCat.setOnClickListener(jenisHewanListener);
        binding.txtJenisOthers.setOnClickListener(jenisHewanListener);
        binding.txtJenisDog.performClick();
    }

    private void observeViewModel() {
        viewModel.salonDetail.observe(getViewLifecycleOwner(), this::displaySalonDetails);

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.scrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }

    private void displaySalonDetails(Salon salon) {
        if (salon == null) return;
        currentSalonData = salon;

        binding.namaSalon.setText(salon.getNama());
        binding.kotaSalon.setText(salon.getKota());
        binding.descSalon.setText("Tempat grooming terpercaya dengan pelayanan lengkap. Buka: " + salon.getJam());

        if (salon.getImageUrl() != null && !salon.getImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(salon.getImageUrl())
                    .placeholder(R.drawable.grace)
                    .error(R.drawable.ic_paw)
                    .into(binding.imgSalon);
        }

        setupLayananTags(salon.getLayanan());
    }


    // Fungsi untuk membuat tag layanan
    private void setupLayananTags(List<String> layananTersedia) {
        binding.layananContainer.removeAllViews(); // Hapus view lama
        layananDipilih.clear(); // Reset pilihan saat view dibuat ulang

        if (layananTersedia == null) return;

        for (String l : layananTersedia) {
            TextView tag = new TextView(requireContext());
            tag.setText(l);
            tag.setTextSize(12f); // Sesuaikan ukuran
            tag.setPadding(24, 12, 24, 12); // Sesuaikan padding
            tag.setTextColor(Color.BLACK);
            tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Default: tidak dipilih
            tag.setGravity(Gravity.CENTER);
            tag.setClickable(true);
            tag.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // Margin antar tag
            lp.setMargins(0, 0, 16, 16); // Margin kanan dan bawah
            tag.setLayoutParams(lp);

            // Listener untuk memilih/batal memilih layanan
            tag.setOnClickListener(v -> {
                if (layananDipilih.contains(l)) {
                    // Jika sudah dipilih -> batalkan pilihan
                    layananDipilih.remove(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Kembali ke state tidak dipilih
                    tag.setTextColor(Color.BLACK);
                } else {
                    // Jika belum dipilih -> pilih
                    layananDipilih.add(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_hijau); // State dipilih
                    tag.setTextColor(Color.WHITE); // Warna teks saat dipilih
                }
            });

            binding.layananContainer.addView(tag);
        }
    }

    private void navigateToBooking(String providerName, String providerId) {
        try {
            DetailSalonFragmentDirections.ActionDetailSalonFragmentToBookingFragment action =
                    DetailSalonFragmentDirections.actionDetailSalonFragmentToBookingFragment(
                            providerName,
                            providerId,
                            currentSalonData.getTipe(),
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
