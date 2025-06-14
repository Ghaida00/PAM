package com.example.projectakhir.newpawpal;

import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private static final String TAG = "ProductAdapter"; // Tambahkan TAG

    private List<Product> productList;
    private OnItemClickListener listener;
    private Map<String, Integer> productQuantities; // Peta internal adapter untuk kuantitas

    public interface OnItemClickListener {
        void onInitialAddToCartClick(Product product); // Untuk klik ikon keranjang awal
        void onItemClick(Product product); // Untuk klik keseluruhan item (detail produk)
        void onAddQuantityClick(Product product); // Untuk menambah kuantitas
        void onRemoveQuantityClick(Product product); // Untuk mengurangi kuantitas
        void onAddToCartFinalClick(Product product, int quantityToSet); // Untuk tombol "Tambahkan ke Keranjang" yang muncul setelah +/-
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
        this.productQuantities = new HashMap<>(); // Inisialisasi peta kuantitas adapter
        Log.d(TAG, "ProductAdapter: Adapter diinisialisasi.");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        // Penting: currentQuantity diambil dari peta internal adapter
        int currentQuantity = product.getId() != null ? productQuantities.getOrDefault(product.getId(), 0) : 0;
        Log.d(TAG, "onBindViewHolder: Binding produk " + product.getName() + " (ID: " + product.getId() + "). Kuantitas saat ini: " + currentQuantity);
        holder.bind(product, listener, currentQuantity);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        Log.d(TAG, "setProducts: Daftar produk diatur ulang. Memanggil notifyDataSetChanged().");
        notifyDataSetChanged();
    }

    public void updateProductQuantity(String productId, int quantity) {
        if (productId != null) {
            Log.d(TAG, "updateProductQuantity: Mengatur kuantitas untuk ID " + productId + " menjadi " + quantity);
            productQuantities.put(productId, quantity); // Update peta internal adapter
            // Temukan posisi produk di list dan update item tersebut agar RecyclerView diperbarui
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId() != null && productList.get(i).getId().equals(productId)) {
                    Log.d(TAG, "updateProductQuantity: Menemukan produk di posisi " + i + ". Memanggil notifyItemChanged().");
                    notifyItemChanged(i); // Ini memicu onBindViewHolder lagi untuk item tersebut
                    return; // Keluar setelah menemukan dan memperbarui
                }
            }
            Log.w(TAG, "updateProductQuantity: Produk dengan ID " + productId + " tidak ditemukan di productList.");
        } else {
            Log.e(TAG, "updateProductQuantity: Product ID adalah NULL.");
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductRating;
        TextView tvProductPrice;
        ImageView ivAddToCartQuick; // Ikon keranjang awal
        Button btnAddToCartSingle; // Tombol "Tambahkan ke Keranjang"
        LinearLayout quantityControlLayout;
        ImageView ivRemoveQuantity;
        TextView tvQuantity;
        ImageView ivAddQuantity;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ivAddToCartQuick = itemView.findViewById(R.id.ivAddToCartQuick);
            btnAddToCartSingle = itemView.findViewById(R.id.btnAddToCartSingle);
            quantityControlLayout = itemView.findViewById(R.id.quantityControlLayout);
            ivRemoveQuantity = itemView.findViewById(R.id.ivRemoveQuantity);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivAddQuantity = itemView.findViewById(R.id.ivAddQuantity);
        }

        void bind(final Product product, final OnItemClickListener listener, int currentQuantity) {
            Log.d(TAG, "bind: Untuk produk " + product.getName() + " (ID: " + product.getId() + "). Kuantitas yang diterima di bind: " + currentQuantity);

            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivProductImage);

            tvProductName.setText(product.getName());

            if (product.getRating() > 0) {
                tvProductRating.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));
                tvProductRating.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.ratingLayout).setVisibility(View.VISIBLE);
            } else {
                tvProductRating.setVisibility(View.GONE);
                itemView.findViewById(R.id.ratingLayout).setVisibility(View.GONE);
            }

            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvProductPrice.setText(formatRupiah.format(product.getPrice()));

            // Logika visibilitas berdasarkan kuantitas di keranjang
            if (currentQuantity > 0) {
                ivAddToCartQuick.setVisibility(View.GONE);          // Sembunyikan ikon keranjang
                quantityControlLayout.setVisibility(View.VISIBLE);  // Tampilkan kontrol kuantitas (+/-)
                btnAddToCartSingle.setVisibility(View.VISIBLE);     // Tampilkan tombol "Tambahkan ke Keranjang"
                tvQuantity.setText(String.valueOf(currentQuantity)); // Set kuantitas saat ini
                Log.d(TAG, "bind: Tampilan diatur ke kontrol kuantitas.");
            } else {
                ivAddToCartQuick.setVisibility(View.VISIBLE);       // Tampilkan ikon keranjang
                quantityControlLayout.setVisibility(View.GONE);     // Sembunyikan kontrol kuantitas
                btnAddToCartSingle.setVisibility(View.GONE);        // Sembunyikan tombol "Tambahkan ke Keranjang"
                Log.d(TAG, "bind: Tampilan diatur ke ikon keranjang awal.");
            }

            // Listener untuk ikon keranjang awal
            ivAddToCartQuick.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInitialAddToCartClick(product);
                }
            });

            // Listener untuk tombol "Tambahkan ke Keranjang" yang muncul bersamaan dengan +/-
            btnAddToCartSingle.setOnClickListener(v -> {
                if (listener != null) {
                    int quantity = Integer.parseInt(tvQuantity.getText().toString());
                    listener.onAddToCartFinalClick(product, quantity);
                }
            });

            // Listener untuk tombol tambah kuantitas
            ivAddQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddQuantityClick(product);
                }
            });

            // Listener untuk tombol kurang kuantitas
            ivRemoveQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveQuantityClick(product);
                }
            });

            // Listener untuk keseluruhan item (untuk detail produk)
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product);
                }
            });
        }
    }
}
