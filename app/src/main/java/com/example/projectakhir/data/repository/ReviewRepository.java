// package com.example.projectakhir.data.repository.ReviewRepository.java

package com.example.projectakhir.data.repository;

import android.content.Context; // NEW: Import Context
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

    /**
     * Mengunggah gambar ulasan sebagai string Base64 ke FirestoreSource.
     * @param context Konteks aplikasi.
     * @param imageUri URI gambar yang akan diunggah.
     * @return Task<String> yang akan selesai dengan string Base64 gambar.
     */
    public Task<String> uploadReviewImage(Context context, Uri imageUri) { // reviewId parameter removed
        return firestoreSource.uploadReviewImage(context, imageUri);
    }

    public Task<Void> submitReview(Review review) {
        return firestoreSource.submitReview(review);
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        return firestoreSource.getProductReviews(productId);
    }
}
