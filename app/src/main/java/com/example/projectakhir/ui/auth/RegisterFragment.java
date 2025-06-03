package com.example.projectakhir.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);

        binding.buttonRegister.setOnClickListener(v -> registerUser());

        binding.textViewLoginLink.setOnClickListener(v ->
                navController.navigate(R.id.action_registerFragment_to_loginFragment)
        );
        binding.buttonBackToLogin.setOnClickListener(v ->
                navController.popBackStack()
        );
    }

    private void registerUser() {
        String fullName = Objects.requireNonNull(binding.editTextFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.editTextEmailRegister.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.editTextPasswordRegister.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(binding.editTextConfirmPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            binding.editTextFullName.setError("Nama lengkap tidak boleh kosong");
            binding.editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.editTextEmailRegister.setError("Email tidak boleh kosong");
            binding.editTextEmailRegister.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmailRegister.setError("Masukkan email yang valid");
            binding.editTextEmailRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPasswordRegister.setError("Kata sandi tidak boleh kosong");
            binding.editTextPasswordRegister.requestFocus();
            return;
        }

        if (password.length() < 6) {
            binding.editTextPasswordRegister.setError("Kata sandi minimal 6 karakter");
            binding.editTextPasswordRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.editTextConfirmPassword.setError("Konfirmasi kata sandi tidak boleh kosong");
            binding.editTextConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editTextConfirmPassword.setError("Kata sandi tidak cocok");
            binding.editTextConfirmPassword.requestFocus();
            return;
        }

        binding.progressBarRegister.setVisibility(View.VISIBLE);
        binding.buttonRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Update profile Firebase Auth (untuk displayName)
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if(profileTask.isSuccessful()){
                                            Log.d(TAG, "User profile updated with displayName.");
                                        }
                                    });


                            // Simpan data pengguna tambahan ke Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullName", fullName);
                            userData.put("email", email);
                            // Anda bisa menambahkan field lain seperti tanggalBergabung, dll.
                            // userData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

                            db.collection("users").document(firebaseUser.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User data successfully written to Firestore.");
                                        binding.progressBarRegister.setVisibility(View.GONE);
                                        binding.buttonRegister.setEnabled(true);
                                        Toast.makeText(getContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                        // Navigasi ke LoginFragment
                                        navController.navigate(R.id.action_registerFragment_to_loginFragment);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error writing user data to Firestore", e);
                                        binding.progressBarRegister.setVisibility(View.GONE);
                                        binding.buttonRegister.setEnabled(true);
                                        // Pengguna dibuat, tapi data gagal disimpan. Bisa ditangani lebih lanjut.
                                        Toast.makeText(getContext(), "Registrasi berhasil, namun gagal menyimpan detail pengguna.", Toast.LENGTH_LONG).show();
                                        navController.navigate(R.id.action_registerFragment_to_loginFragment);
                                    });
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        binding.progressBarRegister.setVisibility(View.GONE);
                        binding.buttonRegister.setEnabled(true);
                        Toast.makeText(getContext(), "Registrasi gagal: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}