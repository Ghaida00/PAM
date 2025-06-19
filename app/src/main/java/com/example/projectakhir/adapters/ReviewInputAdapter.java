package com.example.projectakhir.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewInputAdapter extends RecyclerView.Adapter<ReviewInputAdapter.InputViewHolder> {
    private final List<Product> products;
    private final Map<String, ReviewInput> reviewInputs = new HashMap<>();
    private final Context context;
    private final OnAddImageClickListener addImageClickListener;

    public interface OnAddImageClickListener {
        void onAddImageClick(int position);
    }

    public static class ReviewInput {
        public float rating = 0f;
        public String comment = "";
        public Uri imageUri = null;
    }

    public ReviewInputAdapter(Context context, List<Product> products, OnAddImageClickListener addImageClickListener) {
        this.context = context;
        this.products = products;
        this.addImageClickListener = addImageClickListener;
        for (Product product : products) {
            reviewInputs.put(product.getId(), new ReviewInput());
        }
    }

    @NonNull
    @Override
    public InputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new InputViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputViewHolder holder, int position) {
        Product product = products.get(position);
        ReviewInput input = reviewInputs.get(product.getId());
        holder.bind(product, input, position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Map<String, ReviewInput> getAllReviewInputs() {
        return reviewInputs;
    }

    public void setImageUriForProduct(String productId, Uri uri) {
        if (reviewInputs.containsKey(productId)) {
            reviewInputs.get(productId).imageUri = uri;
            notifyItemChanged(getProductPositionById(productId));
        }
    }

    private int getProductPositionById(String productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(productId)) return i;
        }
        return -1;
    }

    class InputViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgReviewPreview;
        TextView tvProductName, tvProductPrice;
        RatingBar ratingBar;
        EditText etComment;
        Button btnAddImage, btnRemoveImage;

        public InputViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            etComment = itemView.findViewById(R.id.etComment);
            btnAddImage = itemView.findViewById(R.id.btnAddImage);
            imgReviewPreview = itemView.findViewById(R.id.imgReviewPreview);
            btnRemoveImage = itemView.findViewById(R.id.btnRemoveImage);
        }

        public void bind(Product product, ReviewInput input, int position) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(String.format("Rp %,.0f", product.getPrice()));
            
            // Load product image
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.placeholder_image);
            }

            // Setup rating bar
            ratingBar.setOnRatingBarChangeListener(null);
            ratingBar.setRating(input.rating);
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                input.rating = rating;
            });

            // Setup comment
            etComment.setText(input.comment);
            etComment.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    input.comment = etComment.getText().toString();
                }
            });

            // Setup image buttons
            btnAddImage.setOnClickListener(v -> addImageClickListener.onAddImageClick(position));
            
            if (btnRemoveImage != null) {
                btnRemoveImage.setOnClickListener(v -> {
                    input.imageUri = null;
                    notifyItemChanged(position);
                });
            }

            // Show/hide image preview
            if (input.imageUri != null) {
                imgReviewPreview.setVisibility(View.VISIBLE);
                if (btnRemoveImage != null) {
                    btnRemoveImage.setVisibility(View.VISIBLE);
                }
                btnAddImage.setText("Ganti Gambar");
                
                Glide.with(context)
                        .load(input.imageUri)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(imgReviewPreview);
            } else {
                imgReviewPreview.setVisibility(View.GONE);
                if (btnRemoveImage != null) {
                    btnRemoveImage.setVisibility(View.GONE);
                }
                btnAddImage.setText("Tambah Gambar");
            }
        }
    }
} 