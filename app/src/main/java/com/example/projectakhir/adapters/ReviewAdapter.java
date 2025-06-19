package com.example.projectakhir.adapters;

import android.content.Context;
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
    private Context context;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
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

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews != null ? newReviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserAvatar, imgReviewImage;
        TextView tvUserName, tvReviewDate, tvComment;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            imgReviewImage = itemView.findViewById(R.id.imgReviewImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bind(Review review) {
            // Set user name (gunakan userId untuk sementara)
            tvUserName.setText("User " + review.getUserId().substring(0, Math.min(8, review.getUserId().length())));

            // Set review date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id"));
            String dateString = sdf.format(new Date(review.getTimestamp()));
            tvReviewDate.setText(dateString);

            // Set rating
            ratingBar.setRating(review.getRating());

            // Set comment
            tvComment.setText(review.getComment());

            // Set review image if available
            if (review.getImageUrl() != null && !review.getImageUrl().isEmpty()) {
                imgReviewImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(review.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(imgReviewImage);
            } else {
                imgReviewImage.setVisibility(View.GONE);
            }

            // Set user avatar (gunakan placeholder untuk sementara)
            Glide.with(context)
                    .load(R.drawable.profile_placeholder)
                    .circleCrop()
                    .into(imgUserAvatar);
        }
    }
}