package com.example.projectakhir.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation; // Penting: import Navigation
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentRegisterBinding;
import com.example.projectakhir.ui.auth.RegisterViewModel;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // FIXED: Menggunakan ID yang benar dari fragment_register.xml yang baru
        binding.buttonRegister.setOnClickListener(v -> { // Menggunakan buttonRegister dari layout baru
            String firstName = binding.editTextFirstName.getText().toString().trim(); // Menggunakan editTextFirstName
            String lastName = binding.editTextLastName.getText().toString().trim();   // Menggunakan editTextLastName
            String email = binding.editTextEmail.getText().toString().trim();         // Menggunakan editTextEmail
            String username = binding.editTextUsername.getText().toString().trim();   // Menggunakan editTextUsername
            String password = binding.editTextPassword.getText().toString().trim();   // Menggunakan editTextPassword

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                registerViewModel.registerUser(email, password, firstName, lastName, username);
            }
        });

        // FIXED: Menggunakan ID yang benar dari fragment_register.xml yang baru
        binding.Login.setOnClickListener(v -> { // Menggunakan Login dari layout baru
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_registerFragment_to_loginFragment); // Menggunakan action dari nav_graph
        });

        registerViewModel.registerResult.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                // Setelah register berhasil, navigasi ke HomepageFragment dan bersihkan back stack
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_registerFragment_to_blankHomepageFragment); // Menggunakan action dari nav_graph
            } else {
                Toast.makeText(getContext(), "Pendaftaran gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
