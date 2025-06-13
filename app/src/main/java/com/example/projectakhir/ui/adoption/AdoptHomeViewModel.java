package com.example.projectakhir.ui.adoption;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdoptHomeViewModel extends ViewModel {

    private final AdoptionRepository repository;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private final MutableLiveData<List<Hewan>> _newestHewanList = new MutableLiveData<>();
    public LiveData<List<Hewan>> newestHewanList = _newestHewanList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;
    private final MutableLiveData<String> _userName = new MutableLiveData<>();
    public LiveData<String> userName = _userName;

    private final MutableLiveData<String> _userProfileImageUrl = new MutableLiveData<>();
    public LiveData<String> userProfileImageUrl = _userProfileImageUrl;

    public AdoptHomeViewModel() {
        this.repository = new AdoptionRepository();
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        fetchNewestHewan(5); // Fetch 5 newest pets
        loadUserProfile(); // Ambil 3 hewan terbaru sebagai contoh
    }


    public void loadUserProfile() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DocumentReference userDocRef = db.collection("users").document(uid);
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Get data from Firestore
                    String fullName = documentSnapshot.getString("fullName");
                    String username = documentSnapshot.getString("username");
                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                    // Prefer username, fallback to fullName
                    if (fullName != null && !fullName.isEmpty()) {
                        _userName.setValue(fullName);
                    } else if (username != null && !username.isEmpty()) {
                        _userName.setValue(username);
                    } else {
                        _userName.setValue("User");
                    }

                    _userProfileImageUrl.setValue(profileImageUrl);

                } else {
                    // Fallback to Auth display name if Firestore doc doesn't exist
                    _userName.setValue(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Guest");
                    Uri photoUri = firebaseUser.getPhotoUrl();
                    _userProfileImageUrl.setValue(photoUri != null ? photoUri.toString() : null);
                }
            }).addOnFailureListener(e -> {
                // On failure, still try to use Auth data as fallback
                Log.e("AdoptHomeViewModel", "Error fetching user from Firestore.", e);
                _userName.setValue(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Guest");
                Uri photoUri = firebaseUser.getPhotoUrl();
                _userProfileImageUrl.setValue(photoUri != null ? photoUri.toString() : null);
                _error.setValue("Failed to load profile details.");
            });
        } else {
            // No user logged in
            _userName.setValue("Guest");
            _userProfileImageUrl.setValue(null);
        }
    }

    public void fetchNewestHewan(int limit) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.fetchNewestHewan(limit, new AdoptionRepository.FirestoreCallback<List<Hewan>>() {
            @Override
            public void onSuccess(List<Hewan> result) {
                _newestHewanList.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat hewan terbaru: " + e.getMessage());
                _newestHewanList.setValue(new ArrayList<>()); // Set list kosong saat error
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}