package com.example.projectakhir.ui.shop.Review; // PERUBAHAN: dari ui.fragments ke ui.shop

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.adapters.ReviewAdapter;
import com.example.projectakhir.databinding.FragmentReviewBinding;
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.ui.shop.Review.ReviewViewModel; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop

import java.util.ArrayList;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private ReviewViewModel reviewViewModel;
    private String productId;
    private Uri selectedImageUri;
    private ReviewAdapter reviewAdapter;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        binding.reviewImagePreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(selectedImageUri).into(binding.reviewImagePreview);
                    }
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReviews.setAdapter(reviewAdapter);

        if (productId != null && !productId.isEmpty()) {
            reviewViewModel.getProductReviews(productId).observe(getViewLifecycleOwner(), reviews -> {
                if (reviews != null) {
                    reviewAdapter.updateReviews(reviews);
                }
            });
        }

        binding.addImageButton.setOnClickListener(v -> openImageChooser());

        binding.submitReviewButton.setOnClickListener(v -> {
            float rating = binding.reviewRatingBar.getRating();
            String comment = binding.reviewCommentEditText.getText().toString().trim();

            if (productId == null || productId.isEmpty()) {
                Toast.makeText(getContext(), "ID Produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (comment.isEmpty() && selectedImageUri == null) {
                Toast.makeText(getContext(), "Harap berikan komentar atau tambahkan gambar.", Toast.LENGTH_SHORT).show();
            } else if (rating == 0.0f) {
                Toast.makeText(getContext(), "Harap berikan rating.", Toast.LENGTH_SHORT).show();
            } else {
                reviewViewModel.submitReview(productId, rating, comment, selectedImageUri);
            }
        });

        reviewViewModel.reviewSubmissionStatus.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                binding.reviewCommentEditText.setText("");
                binding.reviewRatingBar.setRating(0.0f);
                binding.reviewImagePreview.setVisibility(View.GONE);
                selectedImageUri = null;
                if (productId != null && !productId.isEmpty()) {
                    reviewViewModel.getProductReviews(productId).observe(getViewLifecycleOwner(), reviews -> {
                        if (reviews != null) {
                            reviewAdapter.updateReviews(reviews);
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Gagal mengirim ulasan. Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
