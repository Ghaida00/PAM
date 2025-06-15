package com.example.projectakhir.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projectakhir.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnBackEditProfile.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.btnSaveChanges.setOnClickListener(v -> {
            String firstName = binding.inputFirstName.getText().toString().trim();
            String lastName = binding.inputLastName.getText().toString().trim();
            String username = binding.inputUsername.getText().toString().trim();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(username)) {
                Toast.makeText(getContext(), "First name and username cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.saveChanges(firstName, lastName, username);
        });
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarEditProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSaveChanges.setEnabled(!isLoading);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearMessages();
            }
        });

        viewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.inputFirstName.setText(user.getFirstName());
                binding.inputLastName.setText(user.getLastName());
                binding.inputUsername.setText(user.getUsername());
            }
        });

        viewModel.updateSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                viewModel.clearMessages();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}