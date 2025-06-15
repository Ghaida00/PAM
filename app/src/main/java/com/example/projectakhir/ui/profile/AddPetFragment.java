package com.example.projectakhir.ui.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentAddPetBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddPetFragment extends Fragment {

    private FragmentAddPetBinding binding;
    private AddPetViewModel viewModel;
    private Uri selectedImageUri = null;

    // Variabel untuk dialog multi-pilihan kepribadian
    private String[] personalityOptions;
    private boolean[] checkedPersonalities;
    private final ArrayList<Integer> userSelectedItems = new ArrayList<>();
    private final List<String> finalSelectedPersonalities = new ArrayList<>();


    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imgUploadIcon.setImageURI(selectedImageUri);
                    binding.txtUploadHint.setText("Image Selected!");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddPetBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AddPetViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializePersonalityDialog();
        setupDropdowns();
        setupListeners();
        observeViewModel();
    }

    private void initializePersonalityDialog() {
        personalityOptions = new String[]{"Friendly", "Playful", "Smart", "Lazy", "Silly", "Spoiled", "Fearful"};
        checkedPersonalities = new boolean[personalityOptions.length];
    }

    private void setupDropdowns() {
        // Dropdown untuk Jenis Kelamin
        String[] genders = new String[]{"Jantan", "Betina"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        binding.inputPetSex.setAdapter(genderAdapter);

        // Dropdown untuk Jenis Hewan
        String[] petTypes = new String[]{"Kucing", "Anjing", "Reptil", "Burung"};
        ArrayAdapter<String> petTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, petTypes);
        binding.inputPetJenis.setAdapter(petTypeAdapter);

        // Dropdown untuk Lokasi
        String[] locations = new String[]{"Jakarta", "Surabaya", "Yogyakarta", "Bali"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
        binding.inputPetLocation.setAdapter(locationAdapter);
    }

    private void setupListeners() {
        binding.btnAddPetBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.layoutUploadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Listener untuk memunculkan dialog saat input kepribadian di-klik
        binding.inputPetPersonality.setOnClickListener(v -> showPersonalityDialog());

        binding.btnConfirmAddPet.setOnClickListener(v -> submitForm());
    }

    private void showPersonalityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose up to 3 Personalities");

        // Set item yang sudah terpilih sebelumnya
        // Ini memastikan pilihan user tidak hilang jika dialog dibuka lagi
        Arrays.fill(checkedPersonalities, false);
        userSelectedItems.clear();
        for (String selected : finalSelectedPersonalities) {
            for (int i = 0; i < personalityOptions.length; i++) {
                if (personalityOptions[i].equals(selected)) {
                    checkedPersonalities[i] = true;
                    userSelectedItems.add(i);
                }
            }
        }

        builder.setMultiChoiceItems(personalityOptions, checkedPersonalities, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (userSelectedItems.size() < 3) {
                    userSelectedItems.add(which);
                } else {
                    // Mencegah memilih lebih dari 3
                    Toast.makeText(getContext(), "You can only select up to 3 personalities.", Toast.LENGTH_SHORT).show();
                    // Batalkan centang
                    ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                }
            } else {
                userSelectedItems.remove(Integer.valueOf(which));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            finalSelectedPersonalities.clear();
            StringBuilder stringBuilder = new StringBuilder();
            Collections.sort(userSelectedItems); // Urutkan agar tampilan konsisten
            for (int i = 0; i < userSelectedItems.size(); i++) {
                finalSelectedPersonalities.add(personalityOptions[userSelectedItems.get(i)]);
                stringBuilder.append(personalityOptions[userSelectedItems.get(i)]);
                if (i != userSelectedItems.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            binding.inputPetPersonality.setText(stringBuilder.toString());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Clear All", (dialog, which) -> {
            Arrays.fill(checkedPersonalities, false);
            userSelectedItems.clear();
            finalSelectedPersonalities.clear();
            binding.inputPetPersonality.setText("");
        });

        builder.show();
    }

    private void submitForm() {
        String name = binding.inputPetName.getText().toString().trim();
        String jenis = binding.inputPetJenis.getText().toString().trim();
        String age = binding.inputPetAge.getText().toString().trim();
        String sex = binding.inputPetSex.getText().toString().trim();
        String about = binding.inputPetAbout.getText().toString().trim();
        String weight = binding.inputPetWeight.getText().toString().trim();
        String location = binding.inputPetLocation.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(jenis) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(sex) || TextUtils.isEmpty(about) || TextUtils.isEmpty(weight) ||
                TextUtils.isEmpty(location) || selectedImageUri == null) {
            Toast.makeText(getContext(), "Please fill all fields and upload a photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi kepribadian
        if (finalSelectedPersonalities.isEmpty()) {
            Toast.makeText(getContext(), "Please choose at least one personality.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.addPet(name, jenis, age, sex, about, finalSelectedPersonalities, weight, location, selectedImageUri);
    }

    private void observeViewModel() {
        viewModel.isSubmitting.observe(getViewLifecycleOwner(), isSubmitting -> {
            binding.progressBarAddPet.setVisibility(isSubmitting ? View.VISIBLE : View.GONE);
            binding.btnConfirmAddPet.setEnabled(!isSubmitting);
        });

        viewModel.submissionResult.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.equals("success")) {
                    Toast.makeText(getContext(), "Pet added successfully!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(this).popBackStack();
                } else {
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
                viewModel.clearSubmissionResult();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
