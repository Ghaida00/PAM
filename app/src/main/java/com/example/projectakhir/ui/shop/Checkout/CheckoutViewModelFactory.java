package com.example.projectakhir.ui.shop.Checkout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.ui.auth.AuthManager;

public class CheckoutViewModelFactory implements ViewModelProvider.Factory {
    private final FirestoreSource firestoreSource;
    private final AuthManager authManager;

    public CheckoutViewModelFactory(FirestoreSource firestoreSource, AuthManager authManager) {
        this.firestoreSource = firestoreSource;
        this.authManager = authManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckoutViewModel.class)) {
            return (T) new CheckoutViewModel(firestoreSource, authManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
} 