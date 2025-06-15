package com.example.projectakhir.data.repository;

import android.util.Log;
import com.example.projectakhir.data.Hewan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public ProfileRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void getPetsByOwner(FirestoreCallback<List<Hewan>> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("User not logged in."));
            return;
        }

        // Query ke koleksi "pets" berdasarkan ownerId
        db.collection("pets")
                .whereEqualTo("ownerId", currentUser.getUid())
                .whereEqualTo("adoptionStatus", "none")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Hewan> petList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Hewan pet = document.toObject(Hewan.class); // Gunakan Hewan.class
                        pet.setId(document.getId());
                        petList.add(pet);
                    }
                    callback.onSuccess(petList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user pets", e);
                    callback.onError(e);
                });
    }
}