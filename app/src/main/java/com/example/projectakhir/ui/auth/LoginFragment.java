package com.example.projectakhir.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils; // Import TextUtils
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Import FirebaseUser

import java.util.Objects; // Import Objects

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private NavController navController;
    // private SharedPreferences sharedPreferences; // Dihapus karena kita pakai Firebase Auth
    public static final String PREFS_NAME = "LoginPrefs"; // Bisa tetap dipakai jika ada preferensi lain
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn"; // Dihapus

    private FirebaseAuth mAuth; // Tambahkan FirebaseAuth

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // Dihapus
        mAuth = FirebaseAuth.getInstance(); // Inisialisasi FirebaseAuth
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);

        // Cek jika pengguna sudah login menggunakan Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToBlankHomepage();
            return;
        }

        binding.buttonLogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.editTextEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.editTextPassword.getText()).toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.editTextEmail.setError("Email tidak boleh kosong");
                binding.editTextEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                binding.editTextPassword.setError("Kata sandi tidak boleh kosong");
                binding.editTextPassword.requestFocus();
                return;
            }

            binding.buttonLogin.setEnabled(false); // Nonaktifkan tombol selama proses login
            // Tambahkan ProgressBar jika ada
            // binding.progressBarLogin.setVisibility(View.VISIBLE);


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        binding.buttonLogin.setEnabled(true); // Aktifkan kembali tombol
                        // binding.progressBarLogin.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Login berhasil
                            Toast.makeText(getContext(), "Login Berhasil.", Toast.LENGTH_SHORT).show();
                            navigateToBlankHomepage();
                        } else {
                            // Jika login gagal, tampilkan pesan ke pengguna.
                            Toast.makeText(getContext(), "Login Gagal: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.textViewRegister.setOnClickListener(v -> {
            // Navigasi ke RegisterFragment
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }

    private void navigateToBlankHomepage() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build();
        navController.navigate(R.id.action_loginFragment_to_blankHomepageFragment, null, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}