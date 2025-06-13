package com.example.projectakhir.ui;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserViewModel";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<String> _userName = new MutableLiveData<>();
    public LiveData<String> userName = _userName;

    private final MutableLiveData<String> _profileImageUrl = new MutableLiveData<>();
    public LiveData<String> profileImageUrl = _profileImageUrl;

    public UserViewModel() {
        loadUserProfile();
    }

    public void loadUserProfile() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            DocumentReference userDocRef = db.collection("users").document(firebaseUser.getUid());
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fullName");
                    String username = documentSnapshot.getString("username");
                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                    if (fullName != null && !fullName.isEmpty()) {
                        _userName.setValue(fullName);
                    } else if (username != null && !username.isEmpty()) {
                        _userName.setValue(username);
                    } else {
                        _userName.setValue("Pengguna");
                    }
                    _profileImageUrl.setValue(profileImageUrl);
                } else {
                    _userName.setValue(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Guest");
                    Uri photoUri = firebaseUser.getPhotoUrl();
                    _profileImageUrl.setValue(photoUri != null ? photoUri.toString() : null);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Gagal mengambil data pengguna dari Firestore.", e);
                _userName.setValue(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Guest");
            });
        } else {
            _userName.setValue("Guest");
            _profileImageUrl.setValue(null);
        }
    }
}