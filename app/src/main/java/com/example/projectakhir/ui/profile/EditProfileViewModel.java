package com.example.projectakhir.ui.profile;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileViewModel extends ViewModel {
    private static final String TAG = "EditProfileViewModel";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _updateSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> updateSuccess = _updateSuccess;

    public EditProfileViewModel() {
        loadCurrentUser();
    }

    public void loadCurrentUser() {
        _isLoading.setValue(true);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            _error.setValue("User not logged in.");
            _isLoading.setValue(false);
            return;
        }

        DocumentReference userDocRef = db.collection("users").document(firebaseUser.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User currentUser = documentSnapshot.toObject(User.class);
                _user.setValue(currentUser);
            } else {
                _error.setValue("User data not found in database.");
            }
            _isLoading.setValue(false);
        }).addOnFailureListener(e -> {
            _error.setValue("Failed to load user data: " + e.getMessage());
            _isLoading.setValue(false);
        });
    }

    public void saveChanges(String firstName, String lastName, String username) {
        _isLoading.setValue(true);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            _error.setValue("User not logged in.");
            _isLoading.setValue(false);
            return;
        }
        String uid = firebaseUser.getUid();
        String fullName = (firstName + " " + lastName).trim();

        // 1. Update di Firestore
        DocumentReference userDocRef = db.collection("users").document(uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("username", username);
        updates.put("fullName", fullName);

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // 2. Update di Firebase Auth (Display Name)
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build();

                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                _isLoading.setValue(false);
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated successfully.");
                                    _updateSuccess.setValue(true);
                                } else {
                                    Log.e(TAG, "Failed to update user profile in Auth.", task.getException());
                                    // Meskipun gagal update di Auth, anggap sukses karena Firestore berhasil
                                    _updateSuccess.setValue(true);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    _isLoading.setValue(false);
                    _error.setValue("Failed to save changes: " + e.getMessage());
                });
    }

    public void clearMessages() {
        _error.setValue(null);
        _updateSuccess.setValue(false);
    }
}