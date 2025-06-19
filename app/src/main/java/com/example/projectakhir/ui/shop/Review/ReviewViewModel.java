package com.example.projectakhir.ui.shop.Review; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.data.repository.ReviewRepository;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReviewViewModel extends ViewModel {
    private final ReviewRepository reviewRepository;
    private final RealtimeDbSource realtimeDbSource;
    private final MutableLiveData<Boolean> _reviewSubmissionStatus = new MutableLiveData<>();
    public LiveData<Boolean> reviewSubmissionStatus = _reviewSubmissionStatus;
    
    private final MutableLiveData<List<Product>> _productsToReview = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Product>> productsToReview = _productsToReview;

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

    public void submitMultipleReviews(List<Review> reviews) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                       FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        
        if (userId == null) {
            _reviewSubmissionStatus.setValue(false);
            return;
        }

        int totalReviews = reviews.size();
        final int[] successfulSubmissions = {0};

        for (Review review : reviews) {
            review.setUserId(userId);
            reviewRepository.submitReview(review)
                    .addOnSuccessListener(aVoid -> {
                        successfulSubmissions[0]++;
                        if (successfulSubmissions[0] == totalReviews) {
                            _reviewSubmissionStatus.setValue(true);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ReviewViewModel", "Error submitting review: " + e.getMessage());
                        _reviewSubmissionStatus.setValue(false);
                    });
        }
    }

    public void setProductsToReview(List<Product> products) {
        _productsToReview.setValue(new ArrayList<>(products));
    }

    public LiveData<Integer> getProductsToReviewCount() {
        MutableLiveData<Integer> count = new MutableLiveData<>();
        productsToReview.observeForever(products -> 
            count.setValue(products != null ? products.size() : 0)
        );
        return count;
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        if (_productReviews == null) {
            _productReviews = realtimeDbSource.getProductReviews(productId);
        }
        return _productReviews;
    }
}