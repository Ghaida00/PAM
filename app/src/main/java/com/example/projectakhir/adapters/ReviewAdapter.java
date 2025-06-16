package com.example.projectakhir.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Review;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView reviewerName;
        RatingBar reviewRatingBar;
        TextView reviewComment;
        TextView reviewDate;
        ImageView reviewImage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.reviewer_name);
            reviewRatingBar = itemView.findViewById(R.id.review_rating_bar);
            reviewComment = itemView.findViewById(R.id.review_comment);
            reviewDate = itemView.findViewById(R.id.review_date);
            reviewImage = itemView.findViewById(R.id.review_image);
        }

        public void bind(Review review) {
            // Dalam aplikasi nyata, Anda mungkin akan mengambil nama pengguna dari ID pengguna
            reviewerName.setText("Pengguna: " + review.getUserId().substring(0, 5) + "..."); // Singkat UID
            reviewRatingBar.setRating(review.getRating());
            reviewComment.setText(review.getComment());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            reviewDate.setText(sdf.format(new Date(review.getTimestamp())));

            if (review.getImageUrl() != null && !review.getImageUrl().isEmpty()) {
                reviewImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(review.getImageUrl())
                        .placeholder(R.drawable.ic_placeholder_image)
                        .into(reviewImage);
            } else {
                reviewImage.setVisibility(View.GONE);
            }
        }
    }
}