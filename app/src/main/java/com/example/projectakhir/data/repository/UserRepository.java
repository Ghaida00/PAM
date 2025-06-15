package com.example.projectakhir.data.repository;

import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserRepository {
    private FirestoreSource firestoreSource;

    public UserRepository() {
        // Asumsi FirestoreSource sudah ada dan menangani inisialisasi Firestore
        firestoreSource = new FirestoreSource();
    }

    /**
     * Mengambil data profil pengguna dari Firestore.
     * @param userId ID pengguna Firebase.
     * @return Task<User> yang akan selesai dengan objek User atau null jika tidak ditemukan.
     */
    public Task<User> getUserProfile(String userId) {
        return firestoreSource.getUserDocument(userId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Konversi DocumentSnapshot ke objek User
                            return document.toObject(User.class);
                        }
                    }
                    return null; // Pengguna tidak ditemukan atau task gagal
                });
    }

    // Anda bisa menambahkan metode lain di sini, seperti:
    // public Task<Void> updateAddress(String userId, String newAddress) { ... }
    // public Task<Void> updateProfile(String userId, Map<String, Object> updates) { ... }
}
