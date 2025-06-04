package com.example.projectakhir.ui.profile;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonalDetailViewModel extends ViewModel {
    private static final String TAG = "PersonalDetailVM";

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _profileUpdateSuccess = new MutableLiveData<>();
    public LiveData<Boolean> profileUpdateSuccess = _profileUpdateSuccess;


    public PersonalDetailViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        loadUserDetails();
    }

    public void loadUserDetails() {
        _isLoading.setValue(true);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            String email = firebaseUser.getEmail(); // Email from Auth

            DocumentReference userDocRef = db.collection("users").document(uid);
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // User document exists in Firestore
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String username = documentSnapshot.getString("username");
                    // If fullName was stored during registration, split it or use it
                    String fullName = documentSnapshot.getString("fullName");
                    if (firstName == null && lastName == null && fullName != null) {
                        String[] names = fullName.split(" ", 2);
                        firstName = names[0];
                        lastName = names.length > 1 ? names[1] : "";
                    }
                    if (username == null) { // Fallback for username if not present
                        username = firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "User";
                    }

                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                    User currentUser = new User(uid, firstName, lastName, email, username, profileImageUrl);
                    _user.setValue(currentUser);
                } else {
                    // User document does NOT exist in Firestore, create a basic one or use Auth info
                    Log.w(TAG, "User document does not exist in Firestore for UID: " + uid);
                    String displayName = firebaseUser.getDisplayName();
                    String uFirstName = "", uLastName = "";
                    if (displayName != null && !displayName.isEmpty()) {
                        String[] names = displayName.split(" ", 2);
                        uFirstName = names[0];
                        uLastName = names.length > 1 ? names[1] : "";
                    }
                    User basicUser = new User(uid, uFirstName, uLastName, email, displayName, null);
                    _user.setValue(basicUser);
                    // Optionally, you can prompt the user to complete their profile or create a basic Firestore doc here
                }
                _isLoading.setValue(false);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching user details from Firestore", e);
                _error.setValue("Gagal memuat detail pengguna: " + e.getMessage());
                _isLoading.setValue(false);
            });
        } else {
            _error.setValue("Pengguna tidak login.");
            _isLoading.setValue(false);
        }
    }

    public void updateProfilePicture(Uri imageUri) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null || imageUri == null) {
            _error.setValue("Tidak dapat mengubah foto profil.");
            return;
        }
        _isLoading.setValue(true);
        String uid = firebaseUser.getUid();
        StorageReference profilePicRef = storage.getReference()
                .child("profile_images")
                .child(uid)
                .child("profile.jpg"); // Or a unique name

        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    // Update Firestore with the new image URL
                    db.collection("users").document(uid)
                            .update("profileImageUrl", downloadUrl)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Profile image URL updated in Firestore.");
                                // Update LiveData
                                User currentUser = _user.getValue();
                                if (currentUser != null) {
                                    currentUser.setProfileImageUrl(downloadUrl);
                                    _user.setValue(currentUser);
                                }
                                _profileUpdateSuccess.setValue(true); // Signal success
                                _isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update profile image URL in Firestore", e);
                                _error.setValue("Gagal menyimpan URL foto profil: " + e.getMessage());
                                _isLoading.setValue(false);
                            });
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get download URL", e);
                    _error.setValue("Gagal mendapatkan URL download: " + e.getMessage());
                    _isLoading.setValue(false);
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Profile picture upload failed", e);
                    _error.setValue("Upload foto profil gagal: " + e.getMessage());
                    _isLoading.setValue(false);
                });
    }

    public void clearError() {
        _error.setValue(null);
    }

    public void resetProfileUpdateStatus() {
        _profileUpdateSuccess.setValue(null); // Reset status
    }
}