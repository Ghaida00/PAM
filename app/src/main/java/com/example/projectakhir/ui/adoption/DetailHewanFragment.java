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
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentDetailHewanBinding;

import java.util.ArrayList;

public class DetailHewanFragment extends Fragment {

    private FragmentDetailHewanBinding binding;
    private DetailHewanViewModel viewModel;
    private String hewanIdentifier;
    private Hewan currentHewanData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                hewanIdentifier = DetailHewanFragmentArgs.fromBundle(getArguments()).getNamaHewan();
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

        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        viewModel.hewanDetail.observe(getViewLifecycleOwner(), hewan -> {
            if (hewan != null) {
                currentHewanData = hewan;
                displayHewanDetails(hewan);
                binding.contentLayoutDetailHewan.setVisibility(View.VISIBLE);
                binding.txtErrorDetailHewan.setVisibility(View.GONE);
            } else {
                if (viewModel.isLoading.getValue() != null && !viewModel.isLoading.getValue()){
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

        if (hewanIdentifier != null && !hewanIdentifier.isEmpty()) {
            viewModel.fetchHewanDetailsById(hewanIdentifier);
        }

        binding.btnAdopt.setOnClickListener(v -> {
            if (currentHewanData != null && currentHewanData.getId() != null) {
                try {
                    DetailHewanFragmentDirections.ActionDetailHewanFragmentToFormAdopsiFragment action =
                            DetailHewanFragmentDirections.actionDetailHewanFragmentToFormAdopsiFragment(
                                    currentHewanData.getId(),
                                    currentHewanData.getNama(),
                                    currentHewanData.getJenis(),
                                    currentHewanData.getKota()
                            );
                    NavHostFragment.findNavController(DetailHewanFragment.this).navigate(action);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Navigasi ke Form Adopsi belum siap.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Data hewan belum dimuat atau ID tidak valid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayHewanDetails(Hewan hewan) {
        currentHewanData = hewan;

        if (hewan.getDetailImageUrl() != null && !hewan.getDetailImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(hewan.getDetailImageUrl())
                    .placeholder(R.drawable.grace_no_background)
                    .error(R.drawable.ic_paw)
                    .into(binding.imgHewan);
        } else if (hewan.getThumbnailImageUrl() != null && !hewan.getThumbnailImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(hewan.getThumbnailImageUrl())
                    .placeholder(R.drawable.grace_no_background)
                    .error(R.drawable.ic_paw)
                    .into(binding.imgHewan);
        }
        else {
            binding.imgHewan.setImageResource(R.drawable.grace_no_background);
        }

        binding.namaHewan.setText(hewan.getNama());
        binding.kotaHewan.setText(hewan.getKota());
        binding.detailHewan.setText(getString(R.string.detail_hewan_format, hewan.getGender(), hewan.getUmur(), hewan.getBerat()));
        binding.descHewan.setText(hewan.getDeskripsi());

        if (hewan.getTraits() != null) {
            setupTraitsLayout(new ArrayList<>(hewan.getTraits()));
        } else {
            setupTraitsLayout(new ArrayList<>());
        }
    }

    private void setupTraitsLayout(ArrayList<String> traits) {
        if (traits == null || traits.isEmpty()) {
            binding.traitsLayout.setVisibility(View.GONE);
            return;
        }
        binding.traitsLayout.setVisibility(View.VISIBLE);
        binding.traitsLayout.removeAllViews();

        for (String trait : traits) {
            LinearLayout traitView = createTraitView(trait);
            binding.traitsLayout.addView(traitView);
        }
    }

    private LinearLayout createTraitView(String trait) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(requireContext())
                .inflate(R.layout.item_trait_chip, binding.traitsLayout, false);

        ImageView icon = layout.findViewById(R.id.trait_icon);
        TextView label = layout.findViewById(R.id.trait_label);

        icon.setImageResource(getIconForTrait(trait));
        label.setText(capitalize(trait));

        return layout;
    }

    private int getIconForTrait(String trait) {
        if (trait == null) return R.drawable.ic_paw;
        switch (trait.toLowerCase()) {
            case "friendly": return R.drawable.ic_pawblack;
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