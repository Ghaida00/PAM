package com.example.projectakhir.ui.adoption;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
// Import ProgressBar dan Glide
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentDetailHewanBinding;

import java.util.ArrayList;
import java.util.List; // Import List


public class DetailHewanFragment extends Fragment {

    private FragmentDetailHewanBinding binding;
    private DetailHewanViewModel viewModel; // ViewModel
    private String hewanIdentifier; // Akan menyimpan ID atau nama hewan dari argumen

    // Variabel untuk menyimpan data hewan yang sudah di-fetch
    private Hewan currentHewanData;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                hewanIdentifier = DetailHewanFragmentArgs.fromBundle(getArguments()).getNamaHewan(); // Argumennya bernama namaHewan
            } catch (IllegalArgumentException e) {
                hewanIdentifier = getArguments().getString("namaHewan");
                if (hewanIdentifier == null) {
                    handleArgumentError();
                }
            }
        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen detail hewan tidak ditemukan!", Toast.LENGTH_SHORT).show();
        if (getView() != null && NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.detailHewanFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailHewanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DetailHewanViewModel.class);

        // Observe LiveData dari ViewModel
        viewModel.hewanDetail.observe(getViewLifecycleOwner(), hewan -> {
            if (hewan != null) {
                currentHewanData = hewan; // Simpan data hewan
                displayHewanDetails(hewan);
                binding.contentLayoutDetailHewan.setVisibility(View.VISIBLE); // Tampilkan konten
                binding.txtErrorDetailHewan.setVisibility(View.GONE);
            } else {
                // Jika hewan null setelah loading selesai dan tidak ada error spesifik,
                // mungkin karena tidak ditemukan. Error spesifik ditangani di observer error.
                if (viewModel.isLoading.getValue() != null && !viewModel.isLoading.getValue()){ // Hanya jika tidak sedang loading
                    binding.txtErrorDetailHewan.setText("Data hewan tidak ditemukan.");
                    binding.txtErrorDetailHewan.setVisibility(View.VISIBLE);
                    binding.contentLayoutDetailHewan.setVisibility(View.GONE);
                }
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBarDetailHewan.setVisibility(View.VISIBLE);
                binding.contentLayoutDetailHewan.setVisibility(View.GONE);
                binding.txtErrorDetailHewan.setVisibility(View.GONE);
            } else {
                binding.progressBarDetailHewan.setVisibility(View.GONE);
                // Visibilitas contentLayoutDetailHewan dan txtErrorDetailHewan diatur oleh observer hewanDetail dan error
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                binding.progressBarDetailHewan.setVisibility(View.GONE);
                binding.contentLayoutDetailHewan.setVisibility(View.GONE);
                binding.txtErrorDetailHewan.setText(errorMsg);
                binding.txtErrorDetailHewan.setVisibility(View.VISIBLE);
                viewModel.clearError();
            } else {
                binding.txtErrorDetailHewan.setVisibility(View.GONE);
            }
        });

        // Panggil fetch data jika hewanIdentifier ada
        if (hewanIdentifier != null && !hewanIdentifier.isEmpty()) {
            viewModel.fetchHewanDetailsById(hewanIdentifier);
        }

        // Setup Tombol Adopt
        binding.btnAdopt.setOnClickListener(v -> {
            if (currentHewanData != null && currentHewanData.getId() != null) {
                try {
                    // ▼▼▼ PERUBAHAN DI SINI ▼▼▼
                    // Mengirim semua data yang diperlukan ke FormAdopsiFragment
                    DetailHewanFragmentDirections.ActionDetailHewanFragmentToFormAdopsiFragment action =
                            DetailHewanFragmentDirections.actionDetailHewanFragmentToFormAdopsiFragment(
                                    currentHewanData.getId(),
                                    currentHewanData.getNama(),
                                    currentHewanData.getJenis(),
                                    currentHewanData.getKota()
                            );
                    NavHostFragment.findNavController(DetailHewanFragment.this).navigate(action);
                    // ▲▲▲ AKHIR DARI PERUBAHAN ▲▲▲
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Navigasi ke Form Adopsi belum siap.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Data hewan belum dimuat atau ID tidak valid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayHewanDetails(Hewan hewan) {
        // Simpan data hewan untuk digunakan nanti
        currentHewanData = hewan;

        // Memuat gambar detail menggunakan Glide
        if (hewan.getDetailImageUrl() != null && !hewan.getDetailImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(hewan.getDetailImageUrl())
                    .placeholder(R.drawable.grace_no_background) // Placeholder opsional
                    .error(R.drawable.ic_paw) // Error image opsional
                    .into(binding.imgHewan);
        } else if (hewan.getThumbnailImageUrl() != null && !hewan.getThumbnailImageUrl().isEmpty()) {
            // Fallback ke thumbnail jika detail image URL tidak ada
            Glide.with(requireContext())
                    .load(hewan.getThumbnailImageUrl())
                    .placeholder(R.drawable.grace_no_background)
                    .error(R.drawable.ic_paw)
                    .into(binding.imgHewan);
        }
        else {
            binding.imgHewan.setImageResource(R.drawable.grace_no_background); // Default
        }

        binding.namaHewan.setText(hewan.getNama());
        binding.kotaHewan.setText(hewan.getKota());
        binding.detailHewan.setText(getString(R.string.detail_hewan_format, hewan.getGender(), hewan.getUmur(), hewan.getBerat()));
        binding.descHewan.setText(hewan.getDeskripsi());

        if (hewan.getTraits() != null) {
            setupTraitsLayout(new ArrayList<>(hewan.getTraits())); // Konversi ke ArrayList jika perlu
        } else {
            setupTraitsLayout(new ArrayList<>()); // Berikan list kosong jika null
        }
    }

    // Fungsi untuk setup layout traits (kode tetap sama)
    private void setupTraitsLayout(ArrayList<String> traits) {
        if (traits == null || traits.isEmpty()) {
            binding.traitsLayout.setVisibility(View.GONE);
            return;
        }
        binding.traitsLayout.setVisibility(View.VISIBLE);
        binding.traitsLayout.removeAllViews();

        if (traits.size() == 3) {
            binding.traitsLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout top = new LinearLayout(requireContext());
            top.setGravity(Gravity.CENTER);
            top.addView(createTraitView(traits.get(0)));

            LinearLayout bottom = new LinearLayout(requireContext());
            bottom.setGravity(Gravity.CENTER);
            bottom.setPadding(0, 26, 0, 0);

            LinearLayout traitLeft = createTraitView(traits.get(1));
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            try {
                leftParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.trait_horizontal_margin));
            } catch (Exception e) { leftParams.setMarginEnd(16); /* fallback */ }
            traitLeft.setLayoutParams(leftParams);


            LinearLayout traitRight = createTraitView(traits.get(2));
            LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            try {
                rightParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.trait_horizontal_margin));
            } catch (Exception e) { rightParams.setMarginStart(16); /* fallback */ }
            traitRight.setLayoutParams(rightParams);

            bottom.addView(traitLeft);
            bottom.addView(traitRight);

            binding.traitsLayout.addView(top);
            binding.traitsLayout.addView(bottom);
        } else {
            binding.traitsLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout currentRow = null;
            final int ITEMS_PER_ROW = 3;

            for (int i = 0; i < traits.size(); i++) {
                if (i % ITEMS_PER_ROW == 0) {
                    currentRow = new LinearLayout(requireContext());
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setGravity(Gravity.CENTER);
                    currentRow.setPadding(0, (i == 0 ? 0 : 8), 0, 8);
                    binding.traitsLayout.addView(currentRow);
                }
                if (currentRow != null) {
                    currentRow.addView(createTraitView(traits.get(i)));
                }
            }
        }
    }


    private LinearLayout createTraitView(String trait) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(16, 8, 16, 8);
        layout.setLayoutParams(lp);

        ImageView icon = new ImageView(requireContext());
        icon.setImageResource(getIconForTrait(trait));
        int iconSize = 80; // fallback
        try {
            iconSize = getResources().getDimensionPixelSize(R.dimen.trait_icon_size);
        } catch (Exception e) { /* biarkan default */ }
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));

        TextView label = new TextView(requireContext());
        label.setText(capitalize(trait));
        label.setTextSize(12f);
        label.setTypeface(null, android.graphics.Typeface.BOLD);
        label.setGravity(Gravity.CENTER);
        label.setPadding(0, 4, 0, 0);

        layout.addView(icon);
        layout.addView(label);
        return layout;
    }

    private int getIconForTrait(String trait) {
        if (trait == null) return R.drawable.ic_paw;
        switch (trait.toLowerCase()) {
            case "friendly": return R.drawable.ic_paw;
            case "silly": return R.drawable.traits_cat;
            case "playful": return R.drawable.traits_toy;
            case "spoiled": return R.drawable.traits_spoiled;
            case "lazy": return R.drawable.traits_lazy;
            case "smart": return R.drawable.traits_smart;
            case "fearful": return R.drawable.traits_fearful;
            default: return R.drawable.ic_paw;
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}