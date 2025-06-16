// com.example.projectakhir.auth.AuthManager.java

package com.example.projectakhir.ui.auth;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential; // Penting: Import ini untuk AuthCredential

/**
 * Manajer autentikasi untuk menangani operasi Firebase Authentication.
 */
public class AuthManager {

    private FirebaseAuth firebaseAuth;

    /**
     * Konstruktor untuk AuthManager.
     * Menginisialisasi instance FirebaseAuth.
     */
    public AuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Mendapatkan pengguna Firebase yang saat ini login.
     * @return Objek FirebaseUser jika ada pengguna yang login, atau null jika tidak ada.
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Mendaftarkan pengguna baru dengan email dan password.
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @return Task<AuthResult> yang akan menyelesaikan dengan hasil autentikasi.
     */
    public Task<AuthResult> registerUser(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * Mengotentikasi pengguna dengan email dan password.
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @return Task<AuthResult> yang akan menyelesaikan dengan hasil autentikasi.
     */
    public Task<AuthResult> loginUser(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Mengotentikasi pengguna dengan kredensial tertentu (misalnya, dari Google Sign-In).
     * @param credential Kredensial autentikasi.
     * @return Task<AuthResult> yang akan menyelesaikan dengan hasil autentikasi.
     */
    public Task<AuthResult> signInWithCredential(AuthCredential credential) {
        return firebaseAuth.signInWithCredential(credential);
    }

    /**
     * Melakukan logout pengguna saat ini.
     */
    public void logout() {
        firebaseAuth.signOut();
    }

    // Anda dapat menambahkan metode lain di sini sesuai kebutuhan,
    // seperti reset password, update profil, dll.
}
