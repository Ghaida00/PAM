package com.example.projectakhir.ui.profile; // Sesuaikan dengan package di proyekmu

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

// Import kelas Binding yang sesuai dengan nama file XML kamu
import com.bumptech.glide.Glide;
import com.example.projectakhir.R; // Pastikan R diimport jika perlu
import com.example.projectakhir.databinding.FragmentPersonalDetailBinding;
// Import ViewModel kamu (buat jika belum ada)
// import com.example.projectakhir.ui.profile.PersonalDetailViewModel;
// import com.example.projectakhir.data.model.User; // Model data pengguna

public class PersonalDetailFragment extends Fragment {

    private FragmentPersonalDetailBinding binding;
    private PersonalDetailViewModel viewModel; // Uncomment jika menggunakan ViewModel

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
                            /*// TODO: Tambahkan logika untuk mengunggah atau menyimpan URI gambar ini
                            showToast("Foto profil akan diubah (implementasi backend diperlukan)");*/

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

    // Contoh mengisi data statis, ganti dengan data dari ViewModel
    /*private void populateStaticDataForNow() {
        binding.ivUserProfilePersonal.setImageResource(R.drawable.bambang); // Gambar default
        binding.tvUserFirstName.setText("Kucing");
        binding.tvUserLastName.setText("Oren");
        binding.tvUserEmailValue.setText("ayamoyen@gmail.com");
        binding.tvUsernameValue.setText("Ayam Oyen");
        binding.tvPasswordValue.setText("********"); // Password biasanya tidak ditampilkan nilainya
    }*/

    private void observeViewModelData() {
        // Uncomment dan sesuaikan jika menggunakan ViewModel
        /*
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUserFirstName.setText(user.getFirstName());
                binding.tvUserLastName.setText(user.getLastName());
                binding.tvUserEmailValue.setText(user.getEmail());
                binding.tvUsernameValue.setText(user.getUsername());
                binding.tvPasswordValue.setText("********"); // Selalu tampilkan sebagai bintang

                // Load profile picture
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    // Jika avatarUrl adalah nama drawable resource:
                    int imageResource = getResources().getIdentifier(user.getAvatarUrl(), "drawable", requireContext().getPackageName());
                    if (imageResource != 0) {
                        binding.ivUserProfilePersonal.setImageResource(imageResource);
                    } else {
                        binding.ivUserProfilePersonal.setImageResource(R.drawable.default_profile_picture); // Fallback
                    }
                    // Jika avatarUrl adalah URL gambar dari internet, gunakan Glide atau Picasso:
                    // Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.default_profile_picture).into(binding.ivUserProfilePersonal);
                } else {
                    binding.ivUserProfilePersonal.setImageResource(R.drawable.default_profile_picture); // Fallback
                }
            }
        });

        // Panggil method di ViewModel untuk memuat data pengguna jika belum dimuat
        // viewModel.loadUserDetails("user_id_jika_perlu");
        */

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
                viewModel.resetProfileUpdateStatus(); // Reset status
                // Optionally, reload user details if not automatically updated by LiveData
                // viewModel.loadUserDetails();
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
            // TODO: Implementasikan logika untuk memilih gambar (misalnya dari galeri atau kamera)
            openGallery();
            // showToast("Ubah foto profil diklik");
        });

        // Listener untuk tombol Edit Profile
        binding.btnEditProfile.setOnClickListener(v -> {
            // TODO: Implementasikan navigasi ke halaman Edit Profile atau ubah state halaman ini menjadi mode edit
            showToast("Edit Profile diklik (Navigasi belum diimplementasi)");
            // Contoh Navigasi:
            // NavHostFragment.findNavController(PersonalDetailFragment.this).navigate(R.id.action_personalDetailFragment_to_editProfileFragment);
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