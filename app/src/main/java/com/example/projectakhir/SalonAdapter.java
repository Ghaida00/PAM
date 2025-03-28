package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.ViewHolder> {

    Context context;
    List<Salon> list;

    public SalonAdapter(Context context, List<Salon> list) {
        this.context = context;
        this.list = list;
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
        holder.gambar.setImageResource(s.gambarResId);

        // Tag layanan
        holder.tagContainer.removeAllViews();
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
            lp.setMargins(0, 0, 16, 0);
            tag.setLayoutParams(lp);

            holder.tagContainer.addView(tag);
        }

        // Klik salon → ke halaman detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SalonDetailActivity.class);
            intent.putExtra("nama", s.nama);
            intent.putExtra("kota", s.kota);
            intent.putExtra("jam", s.jam);
            intent.putExtra("layanan", s.layanan);
            intent.putExtra("gambar", s.gambarResId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

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

