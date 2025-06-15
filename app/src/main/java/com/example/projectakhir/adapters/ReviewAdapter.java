package com.example.projectakhir.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Kembalikan Glide
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Review;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews.clear();
        this.reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        CircleImageView reviewerProfileImage;
        TextView reviewerName;
        RatingBar reviewRatingBar;
        TextView reviewComment;
        TextView reviewDate;
        ImageView reviewImage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerProfileImage = itemView.findViewById(R.id.ivProfileImage);
            reviewerName = itemView.findViewById(R.id.tvUsername);
            reviewRatingBar = itemView.findViewById(R.id.rbReviewRating);
            reviewComment = itemView.findViewById(R.id.tvComment);
            reviewDate = itemView.findViewById(R.id.tvReviewDate);
            reviewImage = itemView.findViewById(R.id.ivReviewImage);
        }

        public void bind(Review review) {
            // Profile image
            String profileImageUrl = review.getProfileImageUrl();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                if (isUrl(profileImageUrl)) {
                    Glide.with(itemView.getContext())
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_profile)
                            .into(reviewerProfileImage);
                } else {
                    try {
                        byte[] decodedBytes = Base64.decode(profileImageUrl, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        reviewerProfileImage.setImageBitmap(decodedBitmap);
                    } catch (IllegalArgumentException e) {
                        reviewerProfileImage.setImageResource(R.drawable.ic_profile);
                    }
                }
                reviewerProfileImage.setVisibility(View.VISIBLE);
            } else {
                reviewerProfileImage.setImageResource(R.drawable.ic_profile);
                reviewerProfileImage.setVisibility(View.VISIBLE);
            }

            // Reviewer name
            if (review.getReviewerName() != null && !review.getReviewerName().isEmpty()) {
                reviewerName.setText(review.getReviewerName());
            } else {
                reviewerName.setText("Pengguna: " + review.getUserId().substring(0, Math.min(review.getUserId().length(), 8)) + "...");
            }

            reviewRatingBar.setRating(review.getRating());
            reviewComment.setText(review.getComment());

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            reviewDate.setText(sdf.format(new Date(review.getTimestamp())));

            // Review image
            String reviewImageUrl = review.getImageUrl();
            if (reviewImageUrl != null && !reviewImageUrl.isEmpty()) {
                if (isUrl(reviewImageUrl)) {
                    Glide.with(itemView.getContext())
                            .load(reviewImageUrl)
                            .placeholder(R.drawable.ic_placeholder_image)
                            .into(reviewImage);
                    reviewImage.setVisibility(View.VISIBLE);
                } else {
                    try {
                        byte[] decodedBytes = Base64.decode(reviewImageUrl, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        reviewImage.setImageBitmap(decodedBitmap);
                        reviewImage.setVisibility(View.VISIBLE);
                    } catch (IllegalArgumentException e) {
                        reviewImage.setVisibility(View.GONE);
                    }
                }
            } else {
                reviewImage.setVisibility(View.GONE);
            }
        }
    }

    // Fungsi util untuk cek apakah string adalah URL
    private boolean isUrl(String str) {
        return str != null && (str.startsWith("http://") || str.startsWith("https://"));
    }
}