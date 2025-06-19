package com.example.projectakhir.ui.shop.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.projectakhir.R;
import com.example.projectakhir.adapters.ReviewAdapter;
import com.example.projectakhir.databinding.FragmentPenilaianProdukBinding;
import com.example.projectakhir.ui.shop.Review.ReviewViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PenilaianProdukFragment extends Fragment {

    private FragmentPenilaianProdukBinding binding;
    private ReviewViewModel reviewViewModel;
    private ReviewAdapter reviewAdapter;
    private String productId;

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
        binding = FragmentPenilaianProdukBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        // Setup RecyclerView
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReviews.setAdapter(reviewAdapter);

        // Load reviews
        if (productId != null) {
            loadReviews();
        } else {
            Toast.makeText(getContext(), "Product ID tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReviews() {
        FirebaseFirestore.getInstance().collection("productReviews")
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                java.util.List<com.example.projectakhir.data.model.Review> reviews = new java.util.ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    reviews.add(doc.toObject(com.example.projectakhir.data.model.Review.class));
                }
                android.util.Log.d("ReviewDebug", "Ada " + reviews.size() + " review untuk produk " + productId);
                if (reviews != null && !reviews.isEmpty()) {
                    reviewAdapter.updateReviews(reviews);
                    binding.tvNoReviews.setVisibility(View.GONE);
                    binding.rvReviews.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoReviews.setVisibility(View.VISIBLE);
                    binding.rvReviews.setVisibility(View.GONE);
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 