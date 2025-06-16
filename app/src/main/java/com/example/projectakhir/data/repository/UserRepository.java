package com.example.projectakhir.data.repository;

import com.example.projectakhir.data.source.FirestoreSource;
import com.example.projectakhir.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserRepository {
    private final FirestoreSource firestoreSource;

    public UserRepository() {
        this.firestoreSource = new FirestoreSource();
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

    /**
     * Memperbarui alamat pengguna di Firestore.
     * @param userId ID pengguna Firebase.
     * @param newAddress Alamat baru pengguna.
     * @return Task<Void> yang menandakan keberhasilan atau kegagalan operasi.
     */
    public Task<Void> updateAddress(String userId, String newAddress) {
        return firestoreSource.getUserDocument(userId)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().getReference()
                                .update("address", newAddress);
                    }
                    throw new RuntimeException("Failed to update address");
                });
    }

    /**
     * Memperbarui profil pengguna di Firestore.
     * @param userId ID pengguna Firebase.
     * @param updates Map yang berisi field dan nilai yang akan diperbarui.
     * @return Task<Void> yang menandakan keberhasilan atau kegagalan operasi.
     */
    public Task<Void> updateProfile(String userId, java.util.Map<String, Object> updates) {
        return firestoreSource.getUserDocument(userId)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().getReference()
                                .update(updates);
                    }
                    throw new RuntimeException("Failed to update profile");
                });
    }
}
