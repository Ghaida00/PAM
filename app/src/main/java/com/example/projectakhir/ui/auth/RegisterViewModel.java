// com.example.projectakhir.ui.viewmodel.auth.RegisterViewModel.java

package com.example.projectakhir.ui.auth; // Pastikan paket ini sesuai dengan lokasi ViewModel Anda

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.ui.auth.AuthManager; // Import AuthManager
import com.example.projectakhir.data.firebase.FirestoreSource; // Import FirestoreSource
import com.example.projectakhir.data.model.User; // Import User model
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID; // Untuk menghasilkan UID jika tidak ada dari Firebase

public class RegisterViewModel extends ViewModel {
    private AuthManager authManager;
    private FirestoreSource firestoreSource; // Untuk menyimpan data profil pengguna di Firestore
    private MutableLiveData<Boolean> _registerResult = new MutableLiveData<>();
    public LiveData<Boolean> registerResult = _registerResult;

    public RegisterViewModel() {
        authManager = new AuthManager();
        firestoreSource = new FirestoreSource();
    }

    /**
     * Mendaftarkan pengguna baru dengan email dan password menggunakan Firebase Authentication,
     * lalu menyimpan detail pengguna ke Firestore.
     *
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @param firstName Nama depan pengguna.
     * @param lastName Nama belakang pengguna.
     * @param username Username pengguna.
     */
    public void registerUser(String email, String password, String firstName, String lastName, String username) {
        authManager.registerUser(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                User newUser = new User(uid, firstName, lastName, email, username, null, null);

                                // Simpan detail pengguna ke Firestore
                                firestoreSource.saveUserProfile(newUser)
                                        .addOnCompleteListener(saveTask -> {
                                            if (saveTask.isSuccessful()) {
                                                _registerResult.postValue(true);
                                            } else {
                                                // Jika penyimpanan profil gagal, mungkin ingin menghapus user dari Auth juga
                                                // atau biarkan user mencoba lagi. Untuk saat ini, tandai sebagai gagal.
                                                _registerResult.postValue(false);
                                            }
                                        });
                            } else {
                                _registerResult.postValue(false); // User tidak ditemukan setelah registrasi
                            }
                        } else {
                            _registerResult.postValue(false); // Registrasi Firebase Auth gagal
                        }
                    }
                });
    }
}
