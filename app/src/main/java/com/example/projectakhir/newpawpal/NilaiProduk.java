package com.example.projectakhir.newpawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Import untuk Firebase Realtime Database
import com.example.projectakhir.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;


public class NilaiProduk extends AppCompatActivity {

    private static final String TAG = "NilaiProdukActivity";

    private RecyclerView rvRatingProducts;
    private RatingAdapter ratingAdapter;
    private List<Product> productsToRateList;
    private ImageView backButton;

    // Untuk Firebase
    private FirebaseDatabase database;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nilai_produk);

        rvRatingProducts = findViewById(R.id.rvRatingProducts);
        backButton = findViewById(R.id.backButton);
        TextView tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.nilai_produk_title));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        productsToRateList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(productsToRateList);
        rvRatingProducts.setLayoutManager(new LinearLayoutManager(this));
        rvRatingProducts.setAdapter(ratingAdapter);

        database = FirebaseDatabase.getInstance();
        // Anda perlu menyesuaikan path ini. Idealnya, Anda akan mendapatkan daftar produk yang
        // perlu dinilai dari riwayat pesanan pengguna, bukan semua produk.
        // Untuk contoh ini, saya akan mengambil beberapa produk dummy.
        // Di aplikasi nyata, Anda akan mengirimkan ID produk dari halaman Notifikasi
        // atau Keranjang yang sudah selesai ke Activity ini.
        productsRef = database.getReference("products"); // Path ke node produk Anda

        // Memuat produk yang perlu dinilai
        fetchProductsToRate(); // Metode untuk mengambil produk

        // Contoh tombol Simpan (jika ada di layout Anda)
        findViewById(R.id.btnSimpanPenilaian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRatings();
            }
        });
    }

    private void fetchProductsToRate() {
        // Ini adalah contoh sederhana untuk mengambil beberapa produk.
        // Di aplikasi nyata, Anda akan mengambil ID produk dari Intent
        // yang dikirim dari Notifikasi atau Riwayat Pesanan, lalu
        // mengambil detail produk tersebut dari database.

        // Misalnya, jika Anda ingin mengambil produk berdasarkan ID:
        // String productId1 = getIntent().getStringExtra("product_id_1");
        // String productId2 = getIntent().getStringExtra("product_id_2");
        // if (productId1 != null) {
        //     productsRef.child(productId1).addListenerForSingleValueEvent(new ValueEventListener() { ... });
        // }

        // Untuk demo, kita akan ambil beberapa produk secara acak (atau semua)
        productsRef.limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() { // Ambil 2 produk pertama
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsToRateList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(snapshot.getKey());
                        productsToRateList.add(product);
                    } else {
                        Log.e(TAG, "Gagal mengurai produk dari snapshot: " + snapshot.getKey());
                    }
                }
                ratingAdapter.notifyDataSetChanged();
                if (productsToRateList.isEmpty()) {
                    Toast.makeText(NilaiProduk.this, "Tidak ada produk yang perlu dinilai.", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Produk yang perlu dinilai berhasil diambil. Jumlah: " + productsToRateList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Gagal memuat produk untuk penilaian: " + databaseError.getMessage());
                Toast.makeText(NilaiProduk.this, "Gagal memuat produk untuk penilaian.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRatings() {
        // Logika untuk menyimpan penilaian ke Firebase
        // Anda perlu mengiterasi melalui ratingProductAdapter untuk mendapatkan nilai rating
        // dan komentar yang dimasukkan pengguna untuk setiap produk.

        // Contoh:
        // for (Product product : productsToRateList) {
        //     // Ambil rating dan komentar dari adapter (Anda perlu menambahkan metode di adapter)
        //     // Misalnya: int rating = ratingProductAdapter.getRatingForProduct(product.getId());
        //     // String comment = ratingProductAdapter.getCommentForProduct(product.getId());
        //     // Lalu simpan ke Firebase, mungkin di bawah node 'ratings' atau 'reviews'
        //     // DatabaseReference ratingsRef = database.getReference("ratings").child(product.getId());
        //     // Map<String, Object> review = new HashMap<>();
        //     // review.put("userId", "current_user_id");
        //     // review.put("rating", rating);
        //     // review.put("comment", comment);
        //     // review.put("timestamp", ServerValue.TIMESTAMP);
        //     // ratingsRef.push().setValue(review);
        // }
        Toast.makeText(this, "Penilaian disimpan!", Toast.LENGTH_SHORT).show();
        finish(); // Kembali ke halaman sebelumnya setelah menyimpan
    }
}
