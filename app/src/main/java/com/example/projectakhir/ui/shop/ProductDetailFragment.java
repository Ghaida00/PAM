package com.example.projectakhir.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.databinding.FragmentProductDetailBinding;
import com.google.firebase.database.*;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    private static final String TAG = "ProductDetailFragment";

    private FragmentProductDetailBinding binding;
    private String productId;
    private Product currentProduct;

    private FirebaseDatabase database;
    private DatabaseReference productRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ambil productId dari argument
        productId = getArguments() != null ? getArguments().getString("productId") : null;
        if (productId == null) {
            Toast.makeText(getContext(), "ID produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        database = FirebaseDatabase.getInstance();
        productRef = database.getReference("products").child(productId);


        // Fetch product details
        fetchProductDetails();

        // Tambahkan ke keranjang
        binding.btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                Toast.makeText(getContext(), "Menambahkan " + currentProduct.getName() + " ke keranjang", Toast.LENGTH_SHORT).show();
            }
        });

        // Lihat semua penilaian
        binding.btnSeeAllReviews.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getId() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", currentProduct.getId());
                Navigation.findNavController(v).navigate(R.id.action_productDetailFragment_to_reviewFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Detail produk tidak lengkap untuk melihat penilaian.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductDetails() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(Product.class);
                if (currentProduct != null) {
                    currentProduct.setId(dataSnapshot.getKey());
                    displayProductDetails(currentProduct);
                } else {
                    Toast.makeText(getContext(), "Produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Gagal memuat detail produk: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    private void displayProductDetails(Product product) {
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.productImage);

        binding.productName.setText(product.getName());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        binding.productPrice.setText(formatRupiah.format(product.getPrice()));

        binding.ratingBar.setRating(0f);
        binding.tvRatingText.setText("Belum ada rating");

        binding.productDescription.setText(product.getDescription());
        binding.productStock.setText("Stok: " + product.getStock());

        // Tombol keranjang jika stok habis
        if (product.getStock() <= 0) {
            binding.btnAddToCart.setText("Stok Habis");
            binding.btnAddToCart.setEnabled(false);
            binding.btnAddToCart.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            binding.btnAddToCart.setText("Tambahkan ke Keranjang");
            binding.btnAddToCart.setEnabled(true);
            binding.btnAddToCart.setBackgroundColor(getResources().getColor(R.color.teal_200));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}