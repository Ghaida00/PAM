package com.example.projectakhir.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.R;

import java.util.List;
import java.util.ArrayList;

public class HewanAdapter extends RecyclerView.Adapter<HewanAdapter.ViewHolder> {

    public interface OnHewanClickListener {
        void onHewanClick(Hewan hewan);
    }

    private List<Hewan> list;
    private Context context;
    private final OnHewanClickListener clickListener;

    public HewanAdapter(Context context, List<Hewan> list, OnHewanClickListener listener) {
        this.context = context;
        this.list = new ArrayList<>(list);
        this.clickListener = listener;
    }

    public void updateData(List<Hewan> newList) {
        this.list.clear();
        if (newList != null) {
            this.list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hewan, parent, false); //
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hewan data = list.get(position);
        holder.nama.setText(data.getNama()); // Gunakan getter
        holder.lokasi.setText(data.getKota() + "  •  " + data.getJenis()); // Gunakan getter
        holder.tagUmur.setText(data.getUmur()); // Gunakan getter
        holder.tagGender.setText(data.getGender()); // Gunakan getter

        // --- Modifikasi untuk memuat gambar dari URL menggunakan Glide ---
        if (data.getThumbnailImageUrl() != null && !data.getThumbnailImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(data.getThumbnailImageUrl())
                    .placeholder(R.drawable.grace) // Gambar placeholder opsional
                    .error(R.drawable.ic_paw)       // Gambar error opsional jika gagal load
                    .centerCrop() // atau .fitCenter() sesuai kebutuhan
                    .into(holder.gambar);
        } else {
            // Jika URL null atau kosong, tampilkan placeholder atau gambar default
            holder.gambar.setImageResource(R.drawable.grace); // Atau placeholder lain
        }
        // --- Akhir Modifikasi Gambar ---

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHewanClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, lokasi, tagUmur, tagGender;
        ImageView gambar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.txtNama); //
            lokasi = itemView.findViewById(R.id.txtLokasi); //
            tagUmur = itemView.findViewById(R.id.tagUmur); //
            tagGender = itemView.findViewById(R.id.tagGender); //
            gambar = itemView.findViewById(R.id.imgHewan); //
        }
    }
}