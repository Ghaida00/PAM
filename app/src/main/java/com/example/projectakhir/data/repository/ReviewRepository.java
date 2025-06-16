package com.example.projectakhir.data.repository;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.Review;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class ReviewRepository {
    private FirestoreSource firestoreSource;

    public ReviewRepository() {
        firestoreSource = new FirestoreSource();
    }

    public Task<String> uploadReviewImage(Uri imageUri, String reviewId) {
        return firestoreSource.uploadReviewImage(imageUri, reviewId);
    }

    public Task<Void> submitReview(Review review) {
        return firestoreSource.submitReview(review);
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        return firestoreSource.getProductReviews(productId);
    }
}
