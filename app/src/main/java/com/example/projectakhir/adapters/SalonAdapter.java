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

import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon;

import java.util.List;
import java.util.ArrayList;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.ViewHolder> {

    // +++ Interface Listener +++
    public interface OnSalonClickListener {
        void onSalonClick(Salon salon, String tipe); // Kirim juga tipe (grooming/doctor)
    }
    // ++++++++++++++++++++++++++

    private Context context;
    private List<Salon> list;
    private String tipe; // "grooming" atau "doctor"
    private final OnSalonClickListener clickListener; // Tambahkan listener

    // +++ Modifikasi Constructor +++
    public SalonAdapter(Context context, List<Salon> list, String tipe, OnSalonClickListener listener) {
        this.context = context;
        this.list = new ArrayList<>(list); // Use a new list
        this.tipe = tipe;
        this.clickListener = listener; // Simpan listener
    }
    // ++++++++++++++++++++++++++++++

    // Method updateData (sudah ada)
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

        holder.nama.setText(s.nama);
        holder.kota.setText(s.kota);
        // Load gambar (pastikan gambar ada atau gunakan placeholder)
        if (s.gambarResId != 0) {
            holder.gambar.setImageResource(s.gambarResId);
        } else {
            holder.gambar.setImageResource(R.drawable.grace); // Ganti dengan placeholder Anda
        }


        // Tag layanan
        holder.tagContainer.removeAllViews(); // Hapus tag lama sebelum menambah baru
        if (s.layanan != null) {
            for (String layanan : s.layanan) {
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
                lp.setMargins(0, 0, 16, 8); // Beri margin bawah juga
                tag.setLayoutParams(lp);

                holder.tagContainer.addView(tag);
            }
        }

        // +++ Modifikasi Klik Item -> Panggil Listener +++
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSalonClick(s, tipe); // Panggil listener dengan data salon dan tipe
            }
        });
        // +++++++++++++++++++++++++++++++++++++++++++++++
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // ViewHolder (tidak perlu diubah)
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
