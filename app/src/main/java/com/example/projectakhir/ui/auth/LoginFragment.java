package com.example.projectakhir.ui.auth;

import android.content.Intent;
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
import com.example.projectakhir.databinding.FragmentLoginBinding;
import com.example.projectakhir.ui.auth.LoginViewModel; // FIXED: Mengoreksi jalur import LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Konfigurasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // FIXED: Menggunakan ID yang benar dari fragment_login.xml yang baru (camelCase)
        binding.buttonLogin.setOnClickListener(v -> { // ID di XML: buttonLogin
            String email = binding.editTextUsername.getText().toString().trim(); // ID di XML: editTextUsername
            String password = binding.editTextPassword.getText().toString().trim(); // ID di XML: editTextPassword

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                loginViewModel.loginUser(email, password);
            }
        });

        // FIXED: Menggunakan ID yang benar dari fragment_login.xml yang baru (camelCase)
        // dan NavController untuk navigasi
        binding.textViewRegister.setOnClickListener(v -> { // ID di XML: textViewRegister
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        // FIXED: Menggunakan ID yang benar dari fragment_login.xml yang baru (camelCase)
        binding.buttonGoogleSignIn.setOnClickListener(v -> { // ID di XML: buttonGoogleSignIn
            signInWithGoogle();
        });

        loginViewModel.loginResult.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Login berhasil!", Toast.LENGTH_SHORT).show();
                // Setelah login berhasil, navigasi ke HomepageFragment dan bersihkan back stack
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_loginFragment_to_blankHomepageFragment); // Menggunakan action dari nav_graph
            } else {
                Toast.makeText(getContext(), "Login gagal. Cek kredensial Anda.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Toast.makeText(getContext(), "ID Token Google kosong.", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Google Sign-In gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        // FIXED: Mendelegasikan sign-in Firebase dengan credential Google ke ViewModel
        loginViewModel.signInWithGoogleCredential(credential);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
