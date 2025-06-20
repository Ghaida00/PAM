package com.example.projectakhir.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentDetailPetBinding; // Ganti ke binding yang baru
import java.util.ArrayList;

// Ganti implementasi Fragment. Menggunakan DetailHewanFragment sebagai template.
public class DetailPetFragment extends Fragment {

    private FragmentDetailPetBinding binding; // Ganti tipe binding
    private DetailPetViewModel viewModel; // Ganti tipe ViewModel
    private String petId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Menerima argumen petId dari navigasi
            petId = DetailPetFragmentArgs.fromBundle(getArguments()).getPetId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gunakan binding yang baru
        binding = FragmentDetailPetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gunakan ViewModel yang baru
        viewModel = new ViewModelProvider(this).get(DetailPetViewModel.class);

        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        viewModel.hewanDetail.observe(getViewLifecycleOwner(), this::displayHewanDetails);

        // Setup listener for the new delete button
        binding.btnDeletePet.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarDetailHewan.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.contentLayoutDetailHewan.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.deleteStatus.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(getContext(), "Pet deleted successfully.", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }
            // Error case is handled by the existing error observer
        });

        if (petId != null && !petId.isEmpty()) {
            viewModel.fetchHewanDetailsById(petId);
        } else {
            Toast.makeText(getContext(), "Error: Pet ID tidak valid.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Pet")
                .setMessage("Are you sure you want to delete this pet permanently? This action cannot be undone.")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deletePet();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayHewanDetails(Hewan hewan) {
        if (hewan == null) return;

        if (hewan.getDetailImageUrl() != null && !hewan.getDetailImageUrl().isEmpty()) {
            Glide.with(requireContext()).load(hewan.getDetailImageUrl()).into(binding.imgHewan);
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