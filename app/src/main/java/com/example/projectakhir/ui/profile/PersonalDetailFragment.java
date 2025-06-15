package com.example.projectakhir.ui.profile;

import android.app.Activity;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Untuk ViewModel
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentPersonalDetailBinding;

public class PersonalDetailFragment extends Fragment {

    private FragmentPersonalDetailBinding binding;
    private PersonalDetailViewModel viewModel;

    // Launcher untuk memilih gambar dari galeri
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inisialisasi ActivityResultLauncher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Tampilkan gambar yang dipilih di ImageView
                            binding.ivUserProfilePersonal.setImageURI(selectedImageUri);

                            viewModel.updateProfilePicture(selectedImageUri);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonalDetailBinding.inflate(inflater, container, false);
        // Inisialisasi ViewModel (Uncomment jika menggunakan ViewModel)
        viewModel = new ViewModelProvider(this).get(PersonalDetailViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupActionListeners();
        observeViewModelData(); // Panggil method untuk observe data (Uncomment jika menggunakan ViewModel)
        // populateStaticDataForNow(); // Hapus ini jika sudah menggunakan ViewModel
    }


    private void observeViewModelData() {

        viewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUserFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "N/A");
                binding.tvUserLastName.setText(user.getLastName() != null ? user.getLastName() : "N/A");
                binding.tvUserEmailValue.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                binding.tvUsernameValue.setText(user.getUsername() != null ? user.getUsername() : "N/A");
                binding.tvPasswordValue.setText("********");

                if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getProfileImageUrl())
                            .placeholder(R.drawable.bambang) // Your default placeholder
                            .error(R.drawable.ic_profile) // Your error placeholder
                            .into(binding.ivUserProfilePersonal);
                } else {
                    binding.ivUserProfilePersonal.setImageResource(R.drawable.bambang); // Default if no URL
                }
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarPersonalDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnEditProfile.setEnabled(!isLoading);
            binding.ivCameraIcon.setEnabled(!isLoading);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showToast(error);
                viewModel.clearError(); // Clear error after showing
            }
        });

        viewModel.profileUpdateSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                showToast("Foto profil berhasil diperbarui!");
                viewModel.resetProfileUpdateStatus();
            }
        });
    }

    private void setupActionListeners() {
        // Listener untuk tombol kembali
        binding.btnBackPersonalDetail.setOnClickListener(v -> {
            NavHostFragment.findNavController(PersonalDetailFragment.this).popBackStack();
        });

        // Listener untuk ikon kamera (ubah foto profil)
        binding.ivCameraIcon.setOnClickListener(v -> {
            openGallery();
        });

        // Listener untuk tombol Edit Profile
        binding.btnEditProfile.setOnClickListener(v -> {
            // Navigasi ke EditProfileFragment
            NavHostFragment.findNavController(PersonalDetailFragment.this)
                    .navigate(R.id.action_personalDetailFragment_to_editProfileFragment);
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    // Helper method untuk menampilkan Toast
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting untuk mencegah memory leak
    }
}