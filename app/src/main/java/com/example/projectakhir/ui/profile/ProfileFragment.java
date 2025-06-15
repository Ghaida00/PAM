package com.example.projectakhir.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProfileBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserProfile();
        setupNavigationListeners();
        checkAdminStatus(); // Panggil method untuk memeriksa status admin

        binding.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.txtUserEmail.setText(currentUser.getEmail());
            DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
            userDocRef.get().addOnCompleteListener(task -> {
                if (getContext() == null) {
                    return;
                }
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String fullName = document.getString("fullName");
                        binding.txtUserName.setText(fullName != null ? fullName : "Nama Tidak Ada");
                        String profileImageUrl = document.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .error(R.drawable.agus)
                                    .into(binding.imgUserProfile);
                        } else {
                            binding.imgUserProfile.setImageResource(R.drawable.agus);
                        }
                    } else {
                        Log.d("ProfileFragment", "No such document in Firestore, fallback to Auth data");
                        binding.txtUserName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Nama Tidak Ada");
                        Uri photoUri = currentUser.getPhotoUrl();
                        if (photoUri != null) {
                            Glide.with(requireContext())
                                    .load(photoUri)
                                    .placeholder(R.drawable.ic_profile)
                                    .error(R.drawable.agus)
                                    .into(binding.imgUserProfile);
                        } else {
                            binding.imgUserProfile.setImageResource(R.drawable.agus);
                        }
                    }
                } else {
                    Log.d("ProfileFragment", "get failed with ", task.getException());
                    binding.txtUserName.setText("Gagal Memuat Nama");
                    binding.imgUserProfile.setImageResource(R.drawable.agus);
                }
            });
        } else {
            Toast.makeText(getContext(), "Pengguna tidak login.", Toast.LENGTH_SHORT).show();
            if (isAdded() && NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                    NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.profileFragment) {
                NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
            }
        }
    }

    private void checkAdminStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference userDocRef = db.collection("users").document(user.getUid());
            userDocRef.get().addOnCompleteListener(task -> {
                if (!isAdded() || binding == null) return; // Pastikan fragment masih aktif

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String role = document.getString("role");
                        if ("admin".equals(role)) {
                            binding.itemAdminPanelCard.setVisibility(View.VISIBLE);
                        } else {
                            binding.itemAdminPanelCard.setVisibility(View.GONE);
                        }
                    } else {
                        binding.itemAdminPanelCard.setVisibility(View.GONE);
                    }
                } else {
                    binding.itemAdminPanelCard.setVisibility(View.GONE);
                    Log.e("ProfileFragment", "Gagal memeriksa peran admin: ", task.getException());
                }
            });
        }
    }

    private void setupNavigationListeners() {
        binding.itemPersonalDetailCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_personalDetailFragment, "Detail Personal"));
        binding.itemYourPetCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_yourPetListFragment, "Peliharaan Anda"));
        binding.itemDeliveryAddressCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_deliveryAddressFragment, "Alamat Pengiriman"));
        binding.itemPaymentMethodCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_paymentMethodFragment, "Metode Pembayaran"));

        // Listener untuk panel admin
        if(binding.itemAdminPanelCard != null) {
            binding.itemAdminPanelCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_adminFragment, "Admin Panel"));
        }

        binding.itemAboutCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_aboutAppFragment, "Tentang Aplikasi"));
        binding.itemHelpCard.setOnClickListener(v -> composeEmail());
        binding.itemLogoutCard.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void composeEmail() {
        // Alamat email tujuan dan subjek
        String[] recipients = {"pawpal@pawpal.com"};
        String subject = "Bantuan Aplikasi PawPal";

        // Membuat intent untuk membuka aplikasi email
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // Hanya aplikasi email yang akan merespon
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        // Memeriksa apakah ada aplikasi email yang terpasang di perangkat
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Memberi tahu pengguna jika tidak ada aplikasi email
            Toast.makeText(getContext(), "Tidak ada aplikasi email yang terpasang.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateTo(int actionId, String featureName) {
        try {
            if (isAdded()) { // Pastikan fragment terpasang sebelum navigasi
                NavHostFragment.findNavController(this).navigate(actionId);
            }
        } catch (IllegalArgumentException e) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Fitur " + featureName + " belum diimplementasi atau ID aksi salah.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(getContext(), "Berhasil logout!", Toast.LENGTH_SHORT).show();

                    if (isAdded()) {
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build();
                        NavHostFragment.findNavController(ProfileFragment.this)
                                .navigate(R.id.loginFragment, null, navOptions);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}