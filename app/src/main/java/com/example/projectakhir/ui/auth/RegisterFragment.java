package com.example.projectakhir.ui.auth;

import android.net.Uri;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private NavController navController;
    private static final int MAX_USERNAME_RETRIES = 5;

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

        // Generate URL Gambar Profil menggunakan UI Avatars
        String profileImageUrl = "https://ui-avatars.com/api/?name=" + Uri.encode(fullName) +
                "&size=256&background=random&font-size=0.5&bold=true&format=png&color=000000&length=2";

        // Generate dan cek keunikan username
        generateUniqueUsername(fullName, 0, (uniqueUsername) -> {
            if (uniqueUsername != null) {
                // Jika username unik berhasil digenerasi, lanjutkan dengan registrasi Firebase Auth
                proceedWithFirebaseRegistration(fullName, email, password, uniqueUsername, profileImageUrl);
            } else {
                // Gagal membuat username unik setelah beberapa percobaan
                binding.progressBarRegister.setVisibility(View.GONE);
                binding.buttonRegister.setEnabled(true);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal membuat username unik. Silakan coba lagi atau hubungi support.", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*mAuth.createUserWithEmailAndPassword(email, password)
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
                            String[] names = fullName.split(" ", 2);
                            userData.put("firstName", names[0]);
                            userData.put("lastName", names.length > 1 ? names[1] : "");

                            userData.put("email", email);
                            // Anda bisa menambahkan field lain seperti tanggalBergabung, dll.

                            userData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

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
                });*/
    }

    // Fungsi untuk membuat username dasar dari nama lengkap
    private String generateBaseUsername(String fullName) {
        String base = fullName.toLowerCase()
                .replaceAll("\\s+", "") // Hapus semua spasi
                .replaceAll("[^a-z0-9]", ""); // Hapus karakter non-alphanumeric
        if (base.length() > 15) { // Batasi panjang dasar username
            base = base.substring(0, 15);
        }
        if (base.isEmpty()) {
            base = "user"; // Fallback jika nama hanya berisi karakter non-alphanumeric
        }
        return base;
    }

    // Fungsi rekursif (dengan batasan) untuk menghasilkan username unik
    private void generateUniqueUsername(String fullName, int attempt, UsernameCallback callback) {
        if (attempt >= MAX_USERNAME_RETRIES) {
            callback.onResult(null); // Mencapai batas percobaan
            return;
        }

        String baseUsername = generateBaseUsername(fullName);
        String currentUsernameToTry = baseUsername;
        if (attempt > 0) {
            Random random = new Random();
            // Tambahkan angka acak 4 digit untuk variasi
            currentUsernameToTry = baseUsername + (random.nextInt(8999) + 1000);
        }

        String finalCurrentUsernameToTry = currentUsernameToTry; // Variabel harus effectively final untuk lambda
        db.collection("users").whereEqualTo("username", finalCurrentUsernameToTry).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && snapshot.isEmpty()) {
                            // Username unik, kirim kembali melalui callback
                            callback.onResult(finalCurrentUsernameToTry);
                        } else {
                            // Username sudah ada, coba lagi dengan attempt + 1
                            Log.d(TAG, "Username '" + finalCurrentUsernameToTry + "' sudah ada, mencoba lagi. Percobaan: " + (attempt + 1));
                            generateUniqueUsername(fullName, attempt + 1, callback);
                        }
                    } else {
                        // Error saat mengecek keunikan
                        Log.w(TAG, "Error saat mengecek keunikan username", task.getException());
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal memeriksa username: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                        callback.onResult(null); // Error saat pengecekan
                    }
                });
    }

    // Fungsi untuk melanjutkan proses registrasi ke Firebase Authentication dan Firestore
    private void proceedWithFirebaseRegistration(String fullName, String email, String password, String username, String profileImageUrl) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Update profil Firebase Auth (displayName dan photoURL)
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .setPhotoUri(Uri.parse(profileImageUrl)) // Simpan juga URL gambar yang digenerasi di profil Auth
                                    .build();
                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if(profileTask.isSuccessful()){
                                            Log.d(TAG, "Profil pengguna (Auth) diupdate dengan displayName dan photoURL.");
                                        } else {
                                            Log.w(TAG, "Gagal mengupdate profil Auth.", profileTask.getException());
                                        }
                                    });

                            // Siapkan data untuk disimpan ke Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullName", fullName);
                            String[] names = fullName.split(" ", 2);
                            userData.put("firstName", names[0]);
                            userData.put("lastName", names.length > 1 ? names[1] : "");
                            userData.put("email", email);
                            userData.put("username", username); // Simpan username unik yang digenerasi
                            userData.put("profileImageUrl", profileImageUrl); // Simpan URL gambar profil yang digenerasi
                            userData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
                            userData.put("role", "user");

                            // Simpan data pengguna ke Firestore
                            db.collection("users").document(firebaseUser.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Data pengguna berhasil ditulis ke Firestore.");
                                        binding.progressBarRegister.setVisibility(View.GONE);
                                        binding.buttonRegister.setEnabled(true);
                                        if (getContext() != null) {
                                            Toast.makeText(getContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                        }
                                        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.registerFragment) {
                                            navController.navigate(R.id.action_registerFragment_to_loginFragment);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error saat menulis data pengguna ke Firestore", e);
                                        binding.progressBarRegister.setVisibility(View.GONE);
                                        binding.buttonRegister.setEnabled(true);
                                        if (getContext() != null) {
                                            Toast.makeText(getContext(), "Registrasi berhasil, namun gagal menyimpan detail pengguna.", Toast.LENGTH_LONG).show();
                                        }
                                        // Tetap navigasi ke login agar pengguna bisa masuk
                                        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.registerFragment) {
                                            navController.navigate(R.id.action_registerFragment_to_loginFragment);
                                        }
                                    });
                        }
                    } else {
                        // Jika registrasi Firebase Auth gagal
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        binding.progressBarRegister.setVisibility(View.GONE);
                        binding.buttonRegister.setEnabled(true);
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Registrasi gagal: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Interface callback untuk hasil generasi username asinkron
    interface UsernameCallback {
        void onResult(String username);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}