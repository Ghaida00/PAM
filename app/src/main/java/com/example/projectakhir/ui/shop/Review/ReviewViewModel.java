package com.example.projectakhir.ui.shop.Review;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.data.repository.ReviewRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.UUID;

public class ReviewViewModel extends ViewModel {
    private ReviewRepository reviewRepository;
    private MutableLiveData<Boolean> _reviewSubmissionStatus = new MutableLiveData<>();
    public LiveData<Boolean> reviewSubmissionStatus = _reviewSubmissionStatus;

    private LiveData<List<Review>> _productReviews;

    public ReviewViewModel() {
        reviewRepository = new ReviewRepository();
    }

    public void submitReview(Context context, String productId, float rating, String comment, Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _reviewSubmissionStatus.postValue(false);
            return;
        }
        String userId = currentUser.getUid();
        String reviewId = UUID.randomUUID().toString();

        if (imageUri != null) {
            reviewRepository.uploadReviewImage(context, imageUri)
                    .addOnSuccessListener(imageUrl -> {
                        Review review = new Review(reviewId, productId, userId, rating, comment, System.currentTimeMillis(), imageUrl, null, null);
                        reviewRepository.submitReview(review)
                                .addOnCompleteListener(task -> _reviewSubmissionStatus.postValue(task.isSuccessful()));
                    })
                    .addOnFailureListener(e -> _reviewSubmissionStatus.postValue(false));
        } else {
            Review review = new Review(reviewId, productId, userId, rating, comment, System.currentTimeMillis(), null, null, null);
            reviewRepository.submitReview(review)
                    .addOnCompleteListener(task -> _reviewSubmissionStatus.postValue(task.isSuccessful()));
        }
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        if (_productReviews == null) {
            _productReviews = reviewRepository.getProductReviews(productId);
        }
        return _productReviews;
    }
}
