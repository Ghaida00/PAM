package com.example.projectakhir.ui.shop.Review;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class ReviewFragment extends Fragment implements ReviewInputAdapter.OnAddImageClickListener {

    private FragmentReviewBinding binding;
    private ReviewViewModel reviewViewModel;
    private ArrayList<Product> productsToReview = new ArrayList<>();
    private ReviewInputAdapter reviewInputAdapter;
    private int imagePickPosition = -1;

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
            // Ambil list produk dari argument
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

        // Setup adapter input review
        if (productsToReview != null && !productsToReview.isEmpty()) {
            setupReviewAdapter();
        } else {
            // Jika tidak ada produk untuk review, tampilkan pesan
            Toast.makeText(getContext(), "Tidak ada produk untuk review", Toast.LENGTH_SHORT).show();
            // Navigasi kembali
            Navigation.findNavController(requireView()).navigateUp();
        }

        binding.btnSubmitAllReviews.setOnClickListener(v -> {
            if (reviewInputAdapter != null) {
                reviewViewModel.submitMultipleReviews(reviewInputAdapter.getAllReviewInputs());
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
    }

    private void setupReviewAdapter() {
        reviewInputAdapter = new ReviewInputAdapter(requireContext(), productsToReview, this);
        binding.rvInputReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvInputReviews.setAdapter(reviewInputAdapter);
    }

    private void loadSingleProduct(String productId) {
        // Load single product dari Firebase Realtime Database
        RealtimeDbSource realtimeDbSource = new RealtimeDbSource();

        // Gunakan getViewLifecycleOwnerLiveData agar observe hanya dilakukan saat view masih aktif
        getViewLifecycleOwnerLiveData().observe(this, viewLifecycleOwner -> {
            if (viewLifecycleOwner != null && isAdded() && getView() != null) {
                realtimeDbSource.getProductById(productId).observe(viewLifecycleOwner, product -> {
                    if (product != null) {
                        productsToReview.clear();
                        productsToReview.add(product);
                        setupReviewAdapter();
                    } else {
                        Toast.makeText(getContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                });
            }
        });
    }

    @Override
    public void onAddImageClick(int position) {
        imagePickPosition = position;
        checkPermissionAndPickImage();
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
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
}
