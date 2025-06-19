package com.example.projectakhir.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {
    private final FirebaseAuth firebaseAuth;

    public UserRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Mendapatkan user yang sedang login.
     * @return FirebaseUser atau null jika belum login.
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Mendapatkan UID user yang sedang login.
     * @return UID sebagai String, atau null jika belum login.
     */
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * Mendapatkan email user yang sedang login.
     * @return Email sebagai String, atau null jika belum login.
     */
    public String getCurrentUserEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Mendapatkan display name user yang sedang login.
     * @return Display name sebagai String, atau null jika belum login.
     */
    public String getCurrentUserDisplayName() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getDisplayName() : null;
    }

    /**
     * Logout user dari aplikasi.
     */
    public void signOut() {
        firebaseAuth.signOut();
    }
}
