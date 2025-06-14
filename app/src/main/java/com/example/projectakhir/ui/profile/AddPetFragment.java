package com.example.projectakhir.ui.profile;

import android.app.Activity;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentAddPetBinding;
import java.util.Collections;
import java.util.List;

public class AddPetFragment extends Fragment {

    private FragmentAddPetBinding binding;
    private AddPetViewModel viewModel;
    private Uri selectedImageUri = null;

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

        setupDropdowns();
        setupListeners();
        observeViewModel();
    }

    private void setupDropdowns() {
        // Dropdown untuk Jenis Kelamin
        String[] genders = new String[]{"Jantan", "Betina"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        binding.inputPetSex.setAdapter(genderAdapter);

        // Dropdown untuk Kepribadian
        String[] personalities = new String[]{"Friendly", "Playful", "Smart", "Lazy", "Silly"};
        ArrayAdapter<String> personalityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, personalities);
        binding.inputPetPersonality.setAdapter(personalityAdapter);

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

        binding.btnConfirmAddPet.setOnClickListener(v -> submitForm());
    }

    private void submitForm() {
        String name = binding.inputPetName.getText().toString().trim();
        String age = binding.inputPetAge.getText().toString().trim();
        String sex = binding.inputPetSex.getText().toString().trim();
        String about = binding.inputPetAbout.getText().toString().trim();
        String personality = binding.inputPetPersonality.getText().toString().trim();
        String weight = binding.inputPetWeight.getText().toString().trim();
        String location = binding.inputPetLocation.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(sex) ||
                TextUtils.isEmpty(about) || TextUtils.isEmpty(personality) || TextUtils.isEmpty(weight) ||
                TextUtils.isEmpty(location) || selectedImageUri == null) {
            Toast.makeText(getContext(), "Please fill all fields and upload a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Untuk personality, kita simpan sebagai list dengan satu item
        List<String> personalityList = Collections.singletonList(personality);

        viewModel.addPet(name, age, sex, about, personalityList, weight, location, selectedImageUri);
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