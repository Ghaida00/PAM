package com.example.projectakhir.ui.shop.Review; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.data.repository.ReviewRepository;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReviewViewModel extends ViewModel {
    private ReviewRepository reviewRepository;
    private RealtimeDbSource realtimeDbSource;
    private MutableLiveData<Boolean> _reviewSubmissionStatus = new MutableLiveData<>();
    public LiveData<Boolean> reviewSubmissionStatus = _reviewSubmissionStatus;

    private LiveData<List<Review>> _productReviews;

    public ReviewViewModel() {
        reviewRepository = new ReviewRepository();
        realtimeDbSource = new RealtimeDbSource();
    }

    public void submitReview(String productId, float rating, String comment, Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _reviewSubmissionStatus.postValue(false);
            return;
        }
        String userId = currentUser.getUid();
        String reviewId = UUID.randomUUID().toString();

        if (imageUri != null) {
            reviewRepository.uploadReviewImage(imageUri, reviewId)
                    .addOnSuccessListener(imageUrl -> {
                        Review review = new Review(reviewId, productId, userId, rating, comment, System.currentTimeMillis(), imageUrl);
                        reviewRepository.submitReview(review)
                            .addOnSuccessListener(aVoid -> {
                                android.util.Log.d("ReviewDebug", "Review berhasil dikirim ke Firestore: " + reviewId);
                                _reviewSubmissionStatus.postValue(true);
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("ReviewDebug", "Gagal kirim review ke Firestore: " + e.getMessage());
                                _reviewSubmissionStatus.postValue(false);
                            });
                    })
                    .addOnFailureListener(e -> _reviewSubmissionStatus.postValue(false));
        } else {
            Review review = new Review(reviewId, productId, userId, rating, comment, System.currentTimeMillis(), null);
            reviewRepository.submitReview(review)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("ReviewDebug", "Review berhasil dikirim ke Firestore: " + reviewId);
                    _reviewSubmissionStatus.postValue(true);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ReviewDebug", "Gagal kirim review ke Firestore: " + e.getMessage());
                    _reviewSubmissionStatus.postValue(false);
                });
        }
    }

    public void submitMultipleReviews(Map<String, com.example.projectakhir.adapters.ReviewInputAdapter.ReviewInput> reviewInputs) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _reviewSubmissionStatus.postValue(false);
            return;
        }
        String userId = currentUser.getUid();
        int total = reviewInputs.size();
        int[] successCount = {0};
        int[] failCount = {0};
        for (Map.Entry<String, com.example.projectakhir.adapters.ReviewInputAdapter.ReviewInput> entry : reviewInputs.entrySet()) {
            String productId = entry.getKey();
            com.example.projectakhir.adapters.ReviewInputAdapter.ReviewInput input = entry.getValue();
            String reviewId = UUID.randomUUID().toString();
            if (input.imageUri != null) {
                reviewRepository.uploadReviewImage(input.imageUri, reviewId)
                    .addOnSuccessListener(imageUrl -> {
                        Review review = new Review(reviewId, productId, userId, input.rating, input.comment, System.currentTimeMillis(), imageUrl);
                        reviewRepository.submitReview(review)
                            .addOnSuccessListener(aVoid -> {
                                android.util.Log.d("ReviewDebug", "Review berhasil dikirim ke Firestore: " + reviewId);
                                successCount[0]++;
                                if (successCount[0] + failCount[0] == total) {
                                    _reviewSubmissionStatus.postValue(failCount[0] == 0);
                                }
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("ReviewDebug", "Gagal kirim review ke Firestore: " + e.getMessage());
                                failCount[0]++;
                                if (successCount[0] + failCount[0] == total) {
                                    _reviewSubmissionStatus.postValue(false);
                                }
                            });
                    })
                    .addOnFailureListener(e -> {
                        failCount[0]++;
                        if (successCount[0] + failCount[0] == total) {
                            _reviewSubmissionStatus.postValue(false);
                        }
                    });
            } else {
                Review review = new Review(reviewId, productId, userId, input.rating, input.comment, System.currentTimeMillis(), null);
                reviewRepository.submitReview(review)
                    .addOnSuccessListener(aVoid -> {
                        android.util.Log.d("ReviewDebug", "Review berhasil dikirim ke Firestore: " + reviewId);
                        successCount[0]++;
                        if (successCount[0] + failCount[0] == total) {
                            _reviewSubmissionStatus.postValue(failCount[0] == 0);
                        }
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("ReviewDebug", "Gagal kirim review ke Firestore: " + e.getMessage());
                        failCount[0]++;
                        if (successCount[0] + failCount[0] == total) {
                            _reviewSubmissionStatus.postValue(false);
                        }
                    });
            }
        }
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        if (_productReviews == null) {
            _productReviews = realtimeDbSource.getProductReviews(productId);
        }
        return _productReviews;
    }
}