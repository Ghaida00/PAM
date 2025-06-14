package com.example.projectakhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage; // Gambar profil reviewer
        TextView tvUsername;
        LinearLayout ratingStarsLayout; // Untuk menampilkan bintang rating
        TextView tvComment;
        ImageView ivReviewImage; // Gambar yang diupload reviewer (opsional)

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ratingStarsLayout = itemView.findViewById(R.id.ratingStarsLayout);
            tvComment = itemView.findViewById(R.id.tvComment);
            ivReviewImage = itemView.findViewById(R.id.ivReviewImage);
        }

        void bind(final Review review) {
            // Placeholder untuk gambar profil. Anda bisa memuat gambar profil user
            // yang sebenarnya jika Anda menyimpannya di Firebase Storage atau URL.
            Glide.with(itemView.getContext())
                    .load("https://placehold.co/40x40/B3E0C6/000000?text=P") // Placeholder untuk gambar profil
                    .placeholder(R.drawable.profile_placeholder) // Gambar placeholder default
                    .error(R.drawable.profile_placeholder) // Gambar error default
                    .into(ivProfileImage);

            tvUsername.setText(review.getUsername());
            tvComment.setText(review.getComment());

            // Tampilkan bintang rating
            setStarsRating(review.getRating());

            // Muat gambar ulasan jika ada
            if (review.getImageUrl() != null && !review.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(review.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(ivReviewImage);
                ivReviewImage.setVisibility(View.VISIBLE);
            } else {
                ivReviewImage.setVisibility(View.GONE);
            }
        }

        private void setStarsRating(int rating) {
            // Hapus bintang sebelumnya jika ada
            ratingStarsLayout.removeAllViews();
            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView(itemView.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) (itemView.getContext().getResources().getDisplayMetrics().density * 16), // Ukuran 16dp
                        (int) (itemView.getContext().getResources().getDisplayMetrics().density * 16)
                );
                if (i > 0) {
                    params.setMarginStart((int) (itemView.getContext().getResources().getDisplayMetrics().density * 4)); // Margin 4dp
                }
                star.setLayoutParams(params);
                if (i < rating) {
                    star.setImageResource(R.drawable.star_filled);
                } else {
                    star.setImageResource(R.drawable.star_empty);
                }
                ratingStarsLayout.addView(star);
            }
        }
    }
}
