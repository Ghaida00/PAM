package com.example.projectakhir.newpawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PenilaianProduk extends AppCompatActivity {

    private static final String TAG = "PenilaianProdukActivity";

    private ImageView backButton;
    private ImageView ivProductImagePenilaian;
    private TextView tvProductNamePenilaian;
    private TextView tvProductPricePenilaian;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    private String productId; // ID produk yang ulasannya akan ditampilkan
    private FirebaseDatabase database;
    private DatabaseReference productRef;
    private DatabaseReference reviewsRef; // Referensi ke ulasan produk

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penilaian_produk);

        // Inisialisasi UI
        backButton = findViewById(R.id.backButton);
        ivProductImagePenilaian = findViewById(R.id.ivProductImagePenilaian);
        tvProductNamePenilaian = findViewById(R.id.tvProductNamePenilaian);
        tvProductPricePenilaian = findViewById(R.id.tvProductPricePenilaian);
        rvReviews = findViewById(R.id.rvReviews);
        TextView tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.penilaian_produk_title)); // Mengatur judul

        // Mengambil ID produk dari Intent
        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Toast.makeText(this, "ID produk tidak ditemukan untuk penilaian.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        database = FirebaseDatabase.getInstance();
        productRef = database.getReference("products").child(productId);
        // Asumsikan ulasan disimpan di path /reviews/{productId}/{reviewId}
        reviewsRef = database.getReference("reviews").child(productId);


        // Mengatur listener untuk tombol kembali
        backButton.setOnClickListener(v -> onBackPressed());

        // Setup RecyclerView untuk ulasan
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        // Memuat detail produk dan ulasan
        fetchProductAndReviews();
    }

    private void fetchProductAndReviews() {
        // Ambil detail produk untuk header
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    Glide.with(PenilaianProduk.this)
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(ivProductImagePenilaian);
                    tvProductNamePenilaian.setText(product.getName());
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                    tvProductPricePenilaian.setText(formatRupiah.format(product.getPrice()));
                } else {
                    Toast.makeText(PenilaianProduk.this, "Detail produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Product not found for reviews: " + productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load product details for reviews: " + databaseError.getMessage());
            }
        });

        // Ambil semua ulasan untuk produk ini
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                    } else {
                        Log.e(TAG, "Failed to parse review from snapshot: " + snapshot.getKey());
                    }
                }
                reviewAdapter.setReviews(reviewList);
                Log.d(TAG, "Reviews fetched successfully. Count: " + reviewList.size());

                if (reviewList.isEmpty()) {
                    findViewById(R.id.tvNoReviewsPenilaian).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.tvNoReviewsPenilaian).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load reviews: " + databaseError.getMessage());
                Toast.makeText(PenilaianProduk.this, "Gagal memuat ulasan: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
