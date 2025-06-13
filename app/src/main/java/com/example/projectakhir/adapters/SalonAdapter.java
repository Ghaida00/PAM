// File: main/java/com/example/projectakhir/adapters/SalonAdapter.java
package com.example.projectakhir.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon;
import java.util.ArrayList;
import java.util.List;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.ViewHolder> {
    // ... (Interface dan constructor tidak berubah) ...
    public interface OnSalonClickListener {
        void onSalonClick(Salon salon, String tipe);
    }

    private Context context;
    private List<Salon> list;
    private String tipe;
    private final OnSalonClickListener clickListener;

    public SalonAdapter(Context context, List<Salon> list, String tipe, OnSalonClickListener listener) {
        this.context = context;
        this.list = new ArrayList<>(list);
        this.tipe = tipe;
        this.clickListener = listener;
    }

    public void updateData(List<Salon> newList) {
        this.list.clear();
        if (newList != null) {
            this.list.addAll(newList);
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_salon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Salon s = list.get(position);

        holder.nama.setText(s.getNama());
        holder.kota.setText(s.getKota());

        // Memuat gambar dari URL menggunakan Glide
        if (s.getImageUrl() != null && !s.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(s.getImageUrl())
                    .placeholder(R.drawable.grace) // Gambar placeholder
                    .error(R.drawable.ic_paw)       // Gambar jika error
                    .centerCrop()
                    .into(holder.gambar);
        } else {
            // Fallback jika URL tidak ada
            holder.gambar.setImageResource(R.drawable.grace);
        }

        // Tampilkan tag layanan
        holder.tagContainer.removeAllViews();
        if (s.getLayanan() != null) {
            for (String layanan : s.getLayanan()) {
                TextView tag = new TextView(context);
                tag.setText(layanan);
                tag.setTextSize(12f);
                tag.setPadding(24, 8, 24, 8);
                tag.setTextColor(Color.BLACK);
                tag.setBackgroundResource(R.drawable.bg_tag_kuning);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(0, 0, 16, 8);
                tag.setLayoutParams(lp);
                holder.tagContainer.addView(tag);
            }
        }

        // Set listener klik
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSalonClick(s, tipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // ... (ViewHolder class tidak berubah) ...
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gambar;
        TextView nama, kota;
        LinearLayout tagContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gambar = itemView.findViewById(R.id.imgSalon);
            nama = itemView.findViewById(R.id.namaSalon);
            kota = itemView.findViewById(R.id.kotaSalon);
            tagContainer = itemView.findViewById(R.id.tagLayanan);
        }
    }
}