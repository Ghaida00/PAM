package com.example.projectakhir.ui.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProfileBinding;
import com.example.projectakhir.ui.auth.LoginFragment;

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

        /*viewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.txtUserName.setText(user.name);
                binding.txtUserEmail.setText(user.email);
                if (user.avatarUrl != null && !user.avatarUrl.isEmpty()){
                    int imageResource = getResources().getIdentifier(user.avatarUrl, "drawable", requireContext().getPackageName());
                    if (imageResource != 0) {
                        binding.imgUserProfile.setImageResource(imageResource);
                    } else {
                        binding.imgUserProfile.setImageResource(R.drawable.agus);
                    }
                } else {
                    binding.imgUserProfile.setImageResource(R.drawable.agus);
                }
            }
        });*/

        loadUserProfile();

        setupNavigationListeners();

        /*viewModel.navigateTo.observe(getViewLifecycleOwner(), event -> {
            ProfileViewModel.NavigationTarget target = event.getContentIfNotHandled();
            if (target != null) {
                handleNavigation(target);
            }
        });*/

        binding.btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.txtUserEmail.setText(currentUser.getEmail());
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                binding.txtUserName.setText(currentUser.getDisplayName());
            } else {
                DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
                userDocRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String fullName = document.getString("fullName");
                            binding.txtUserName.setText(fullName != null ? fullName : "Nama Tidak Ada");
                        } else {
                            Log.d("ProfileFragment", "No such document in Firestore");
                            binding.txtUserName.setText("Nama Tidak Ada");
                        }
                    } else {
                        Log.d("ProfileFragment", "get failed with ", task.getException());
                        binding.txtUserName.setText("Gagal Memuat Nama");
                    }
                });
            }

            binding.imgUserProfile.setImageResource(R.drawable.agus);
        } else {
            Toast.makeText(getContext(), "Pengguna tidak login.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
        }
    }

    /*private void setupNavigationListeners() {
        binding.itemPersonalDetailCard.setOnClickListener(v -> viewModel.onPersonalDetailClicked());
        binding.itemYourPetCard.setOnClickListener(v -> viewModel.onYourPetClicked());
        binding.itemDeliveryAddressCard.setOnClickListener(v -> viewModel.onDeliveryAddressClicked());
        binding.itemPaymentMethodCard.setOnClickListener(v -> viewModel.onPaymentMethodClicked());
        binding.itemAboutCard.setOnClickListener(v -> viewModel.onAboutClicked());
        binding.itemHelpCard.setOnClickListener(v -> viewModel.onHelpClicked());
        binding.itemLogoutCard.setOnClickListener(v -> viewModel.onLogoutClicked());
    }*/

    private void setupNavigationListeners() {
        binding.itemPersonalDetailCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_personalDetailFragment, "Detail Personal"));
        binding.itemYourPetCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_yourPetListFragment, "Peliharaan Anda"));
        binding.itemDeliveryAddressCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_deliveryAddressFragment, "Alamat Pengiriman"));
        binding.itemPaymentMethodCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_paymentMethodFragment, "Metode Pembayaran"));
        binding.itemAboutCard.setOnClickListener(v -> navigateTo(R.id.action_profileFragment_to_aboutAppFragment, "Tentang Aplikasi"));
        binding.itemHelpCard.setOnClickListener(v -> Toast.makeText(getContext(), "Bantuan diklik (Fitur belum diimplementasi)", Toast.LENGTH_SHORT).show());
        binding.itemLogoutCard.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void navigateTo(int actionId, String featureName) {
        try {
            NavHostFragment.findNavController(this).navigate(actionId);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Fitur " + featureName + " belum diimplementasi atau ID aksi salah.", Toast.LENGTH_LONG).show();
        }
    }

    /*private void handleNavigation(ProfileViewModel.NavigationTarget target) {
        try {
            switch (target) {
                case PERSONAL_DETAIL:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_personalDetailFragment);
                    break;
                case YOUR_PET:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_yourPetListFragment);
                    break;
                case DELIVERY_ADDRESS:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_deliveryAddressFragment);
                    break;
                case PAYMENT_METHOD:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_paymentMethodFragment);
                    break;
                case ABOUT:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_aboutAppFragment);
                    break;
                case HELP:
                    Toast.makeText(getContext(), "Help clicked (Fitur belum diimplementasi)", Toast.LENGTH_SHORT).show();
                    break;
                case LOGOUT:
                    showLogoutConfirmationDialog();
                    break;
            }
        } catch (IllegalArgumentException e) {
            String message = "Navigasi untuk " + target.name() + " belum diimplementasi atau ID action salah.";
            if (getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }*/

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return;

        new AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah kamu yakin ingin logout?")
            .setPositiveButton("Logout", (dialog, which) -> {
                mAuth.signOut();

                Toast.makeText(getContext(), "Berhasil logout!", Toast.LENGTH_SHORT).show(); //

                NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build();
                NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.loginFragment, null, navOptions);
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