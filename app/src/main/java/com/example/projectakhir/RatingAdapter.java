package com.example.projectakhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingProductViewHolder> {

    private List<Product> productsToRateList; // Menggunakan model Product yang sudah ada

    public RatingAdapter(List<Product> productsToRateList) {
        this.productsToRateList = productsToRateList;
    }

    @NonNull
    @Override
    public RatingProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new RatingProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingProductViewHolder holder, int position) {
        Product product = productsToRateList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productsToRateList.size();
    }

    // ViewHolder untuk setiap item produk yang akan dinilai
    static class RatingProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;
        LinearLayout ratingStarsLayout; // Layout untuk bintang penilaian
        EditText etComment; // EditText untuk komentar
        ImageView ivAddPhoto; // ImageView untuk tambah foto

        RatingProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ratingStarsLayout = itemView.findViewById(R.id.ratingStarsLayout);
            etComment = itemView.findViewById(R.id.etComment);
            ivAddPhoto = itemView.findViewById(R.id.ivAddPhoto);

            // Inisialisasi listener untuk bintang (Contoh: 5 bintang)
            // Anda perlu menambahkan ImageView untuk setiap bintang di item_rating_product.xml
            // Dan menyesuaikan logika di sini untuk menangani klik bintang.
            for (int i = 0; i < ratingStarsLayout.getChildCount(); i++) {
                ImageView star = (ImageView) ratingStarsLayout.getChildAt(i);
                final int starIndex = i + 1; // 1-based index
                star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Atur tampilan bintang sesuai dengan klik
                        setStarsRating(starIndex);
                        // Simpan rating ke model Product atau Map di Adapter jika diperlukan
                        Toast.makeText(itemView.getContext(), "Rating: " + starIndex + " bintang", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            ivAddPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Tambah foto untuk produk: " + tvProductName.getText(), Toast.LENGTH_SHORT).show();
                    // Implementasi logika pilih/ambil gambar dari galeri/kamera
                }
            });
        }

        void bind(final Product product) {
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivProductImage);

            tvProductName.setText(product.getName());

            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvProductPrice.setText(formatRupiah.format(product.getPrice()));

            // Reset rating dan komentar saat bind agar tidak ada data dari item sebelumnya
            setStarsRating(0); // Set semua bintang ke kosong
            etComment.setText("");

            // Opsional: Jika Anda ingin memuat rating dan komentar yang sudah ada (misal dari draft)
            // etComment.setText(product.getSavedComment());
            // setStarsRating(product.getSavedRating());
        }

        private void setStarsRating(int rating) {
            for (int i = 0; i < ratingStarsLayout.getChildCount(); i++) {
                ImageView star = (ImageView) ratingStarsLayout.getChildAt(i);
                if (i < rating) {
                    star.setImageResource(R.drawable.star_filled); // Ganti dengan ikon bintang terisi
                } else {
                    star.setImageResource(R.drawable.star_empty); // Ganti dengan ikon bintang kosong
                }
            }
        }
    }
}
