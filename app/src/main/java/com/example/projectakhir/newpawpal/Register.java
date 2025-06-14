package com.example.projectakhir.newpawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Register";

    private EditText etFirstName, etLastName, etEmail, etUsername, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink; // Ini adalah TextView dengan ID 'Login' di XML

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi UI elements
        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        etEmail = findViewById(R.id.editTextEmail);
        etUsername = findViewById(R.id.editTextUsername); // Anda punya ini, tapi mungkin tidak dipakai langsung untuk Firebase Auth
        etPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.buttonRegister);
        tvLoginLink = findViewById(R.id.Login); // ID ini sesuai dengan XML Anda

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set click listeners
        btnRegister.setOnClickListener(this);
        tvLoginLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.buttonRegister) {
            registerUser(etEmail.getText().toString(), etPassword.getText().toString());
        } else if (id == R.id.Login) { // ID ini sesuai dengan TextView yang mengarahkan ke Login
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish(); // Mengakhiri activity Register agar tidak bisa kembali dengan tombol back
        }
    }

    private void registerUser(String email, String password) {
        Log.d(TAG, "registerUser:" + email);
        if (!validateForm()) {
            return;
        }

        // Buat user baru dengan email dan password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Pendaftaran berhasil, update UI dengan informasi user yang sudah login
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Pendaftaran berhasil untuk: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            // Setelah pendaftaran berhasil, arahkan ke halaman utama atau login
                            Intent intent = new Intent(Register.this, Katalog.class); // Atau ke Login.class
                            startActivity(intent);
                            finish(); // Tutup Register Activity
                        } else {
                            // Jika pendaftaran gagal, tampilkan pesan ke pengguna
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Pendaftaran gagal: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Wajib diisi.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Wajib diisi.");
            valid = false;
        } else if (password.length() < 6) { // Firebase membutuhkan password minimal 6 karakter
            etPassword.setError("Password minimal 6 karakter.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        String firstName = etFirstName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Wajib diisi.");
            valid = false;
        } else {
            etFirstName.setError(null);
        }

        String lastName = etLastName.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Wajib diisi.");
            valid = false;
        } else {
            etLastName.setError(null);
        }

        // Anda bisa menambahkan validasi untuk username jika diperlukan,
        // meskipun Firebase Auth tidak menggunakan username untuk pendaftaran dasar.
        // String username = etUsername.getText().toString();
        // if (TextUtils.isEmpty(username)) {
        //     etUsername.setError("Wajib diisi.");
        //     valid = false;
        // } else {
        //     etUsername.setError(null);
        // }

        return valid;
    }
}