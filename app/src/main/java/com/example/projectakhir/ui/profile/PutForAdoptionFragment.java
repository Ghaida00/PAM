package com.example.projectakhir.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentPutForAdoptionBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PutForAdoptionFragment extends Fragment {

    private FragmentPutForAdoptionBinding binding;
    private PutForAdoptionViewModel viewModel;
    private List<Hewan> userPetList = new ArrayList<>();
    private Hewan selectedPet = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPutForAdoptionBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PutForAdoptionViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnPutAdoptionBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.dropdownSelectPet.setOnItemClickListener((parent, view, position, id) -> {
            selectedPet = userPetList.get(position);
            binding.btnConfirmPutAdoption.setEnabled(true);
        });

        binding.btnConfirmPutAdoption.setOnClickListener(v -> {
            if (selectedPet != null) {
                viewModel.listPetForAdoption(selectedPet.getId());
            } else {
                Toast.makeText(getContext(), "Please select a pet first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarPutAdoption.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnConfirmPutAdoption.setEnabled(!isLoading && selectedPet != null);
        });

        viewModel.myPets.observe(getViewLifecycleOwner(), pets -> {
            if (pets != null && !pets.isEmpty()) {
                this.userPetList = pets;
                List<String> petNames = pets.stream().map(Hewan::getNama).collect(Collectors.toList());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, petNames);
                binding.dropdownSelectPet.setAdapter(adapter);
            } else {
                binding.dropdownSelectPet.setText("You have no pets to list");
                binding.dropdownSelectPet.setEnabled(false);
                binding.btnConfirmPutAdoption.setEnabled(false);
            }
        });

        viewModel.submissionResult.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.equals("success")) {
                    Toast.makeText(getContext(), "Your pet is now up for adoption!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(this).popBackStack();
                } else {
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
                viewModel.clearSubmissionResult();
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}