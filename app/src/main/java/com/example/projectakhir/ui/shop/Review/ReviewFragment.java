package com.example.projectakhir.ui.shop.Review;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.adapters.ReviewAdapter;
import com.example.projectakhir.adapters.ReviewInputAdapter;
import com.example.projectakhir.databinding.FragmentReviewBinding;
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.ui.shop.Review.ReviewViewModel;
import com.example.projectakhir.data.firebase.RealtimeDbSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.example.projectakhir.adapters.ReviewInputAdapter.ReviewInput;

public class ReviewFragment extends Fragment implements ReviewInputAdapter.OnAddImageClickListener {

    private FragmentReviewBinding binding;
    private ReviewViewModel reviewViewModel;
    private ArrayList<Product> productsToReview = new ArrayList<>();
    private ReviewInputAdapter reviewInputAdapter;
    private int imagePickPosition = -1;
    private static final String TAG = "ProductDetailFetcher";

    // Permission launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(getContext(), "Permission diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // Image picker launcher
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && imagePickPosition != -1) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Product product = productsToReview.get(imagePickPosition);
                        reviewInputAdapter.setImageUriForProduct(product.getId(), data.getData());
                        Toast.makeText(getContext(), "Gambar berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi productsToReview sebagai ArrayList kosong
        productsToReview = new ArrayList<>();
        
        if (getArguments() != null) {
            // Cek apakah ada array productIds dari notifikasi
            String[] productIds = getArguments().getStringArray("productIds");
            if (productIds != null && productIds.length > 0) {
                // Load semua produk dari array productIds
                for (String productId : productIds) {
                    loadSingleProduct(productId.trim()); // trim untuk menghilangkan whitespace
                }
            } else {
                // Jika tidak ada productIds array, coba cara lama
                ArrayList<Product> products = (ArrayList<Product>) getArguments().getSerializable("productsToReview");
                if (products != null && !products.isEmpty()) {
                    productsToReview = products;
                } else {
                    // Jika tidak ada productsToReview, coba ambil single productId
                    String productId = getArguments().getString("productId");
                    if (productId != null) {
                        // Load single product dari Firebase
                        loadSingleProduct(productId);
                    }
                }
            }
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

        // Setup back button
        binding.backButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());

        binding.btnSubmitAllReviews.setOnClickListener(v -> {
            if (reviewInputAdapter != null) {
                // Paksa update semua EditText ke ReviewInput
                binding.rvInputReviews.clearFocus();
                
                // Convert ReviewInput to Review objects
                List<Review> reviewList = new ArrayList<>();
                Map<String, ReviewInput> inputMap = reviewInputAdapter.getAllReviewInputs();

                for (Map.Entry<String, ReviewInput> entry : inputMap.entrySet()) {
                    String productId = entry.getKey();
                    ReviewInput input = entry.getValue();

                    Review review = new Review(
                        UUID.randomUUID().toString(),
                        productId,
                        null, // userId akan di-set oleh ViewModel
                        input.getRating(),
                        input.getComment(),
                        System.currentTimeMillis(),
                        input.getImageUrl() // atau null jika tak ada gambar
                    );

                    reviewList.add(review);
                }

                reviewViewModel.submitMultipleReviews(reviewList);
            }
        });

        reviewViewModel.reviewSubmissionStatus.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Semua ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.catalogFragment);
            } else {
                Toast.makeText(getContext(), "Gagal mengirim ulasan. Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe productsToReview size changes
        reviewViewModel.getProductsToReviewCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0) {
                binding.rvInputReviews.setVisibility(View.VISIBLE);
                binding.btnSubmitAllReviews.setVisibility(View.VISIBLE);
                binding.rvReviews.setVisibility(View.GONE);
                binding.tvNoReviewsPenilaian.setVisibility(View.GONE);
                setupReviewAdapter();
            } else {
                binding.rvInputReviews.setVisibility(View.GONE);
                binding.btnSubmitAllReviews.setVisibility(View.GONE);
                binding.rvReviews.setVisibility(View.GONE);
                binding.tvNoReviewsPenilaian.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupReviewAdapter() {
        reviewInputAdapter = new ReviewInputAdapter(
            0f, // default rating
            "", // default comment
            null, // default imageUrl
            requireContext(),
            productsToReview,
            this
        );
        binding.rvInputReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvInputReviews.setAdapter(reviewInputAdapter);
    }

    private void loadSingleProduct(String productId) {
        RealtimeDbSource realtimeDbSource = new RealtimeDbSource();
        realtimeDbSource.getProductById(productId).observe(this, product -> {
            if (product != null) {
                if (!productsToReview.contains(product)) {
                    productsToReview.add(product);
                    reviewViewModel.setProductsToReview(productsToReview);
                    Log.d("ReviewFragment", "Product loaded: " + product.getName());
                }
            } else {
                Log.e("ReviewFragment", "Product not found: " + productId);
            }
        });
    }

    @Override
    public void onAddImageClick(int position) {
        imagePickPosition = position;
        checkPermissionAndPickImage();
    }

    private void checkPermissionAndPickImage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void submitReview(Review review) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("productReviews").document(review.getId()).set(review)
            .addOnSuccessListener(aVoid -> android.util.Log.d("ReviewDebug", "Review " + review.getId() + " berhasil dikirim"))
            .addOnFailureListener(e -> android.util.Log.e("ReviewDebug", "Gagal kirim review: " + e.getMessage()));
    }

    // Fungsi untuk mengambil detail produk dari Firebase Realtime Database
    private void fetchProductDetails(String productId) {
        if (productId == null || productId.isEmpty()) {
            android.util.Log.e(TAG, "Product ID is null or empty, cannot fetch details.");
            return;
        }

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("productsToReview")) {
            List<Product> productList = (List<Product>) bundle.getSerializable("productsToReview");
            Log.d("ReviewFragment", "Isi bundle: " + productList);
            if (productList != null) {
                Log.d("ReviewFragment", "Jumlah produk: " + productList.size());
            } else {
                Log.d("ReviewFragment", "Bundle ada, tapi productList null");
            }
        }


        com.google.firebase.database.DatabaseReference productRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("products").child(productId);

        productRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot productSnapshot) {
                if (productSnapshot.exists()) {
                    String productName = productSnapshot.child("name").getValue(String.class);
                    Long productPrice = productSnapshot.child("price").getValue(Long.class);
                    String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                    String category = productSnapshot.child("category").getValue(String.class);
                    String description = productSnapshot.child("description").getValue(String.class);
                    Long stock = productSnapshot.child("stock").getValue(Long.class);

                    android.util.Log.d(TAG, "Product Details for " + productId + ":");
                    android.util.Log.d(TAG, "Name: " + productName);
                    android.util.Log.d(TAG, "Price: " + productPrice);
                    android.util.Log.d(TAG, "Image URL: " + imageUrl);
                    android.util.Log.d(TAG, "Category: " + category);
                    android.util.Log.d(TAG, "Description: " + description);
                    android.util.Log.d(TAG, "Stock: " + stock);

                } else {
                    android.util.Log.d(TAG, "Product with ID " + productId + " does not exist in the database.");
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                android.util.Log.e(TAG, "Error fetching product details for " + productId + ": " + databaseError.getMessage());
            }
        });
    }
}
