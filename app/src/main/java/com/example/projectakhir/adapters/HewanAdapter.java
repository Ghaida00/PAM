package com.example.projectakhir.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.R;

import java.util.List;
import java.util.ArrayList;

public class HewanAdapter extends RecyclerView.Adapter<HewanAdapter.ViewHolder> {

    // +++ Interface untuk menangani klik item hewan +++
    public interface OnHewanClickListener {
        void onHewanClick(Hewan hewan);
    }
    // ++++++++++++++++++++++++++++++++++++++++++++++++

    private List<Hewan> list;
    private Context context;
    private final OnHewanClickListener clickListener; // Tambahkan variabel listener

    // +++ Modifikasi Constructor untuk menerima listener +++
    public HewanAdapter(Context context, List<Hewan> list, OnHewanClickListener listener) {
        this.context = context;
        // Gunakan list baru untuk keamanan
        this.list = new ArrayList<>(list);
        this.clickListener = listener; // Simpan listener
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++

    // Metode untuk update data (sudah ada dan bagus)
    public void updateData(List<Hewan> newList) {
        this.list.clear();
        if (newList != null) {
            this.list.addAll(newList);
        }
        notifyDataSetChanged(); // Notify adapter the data has changed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hewan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hewan data = list.get(position);
        holder.nama.setText(data.nama);
        holder.lokasi.setText(data.kota + "  •  " + data.jenis);
        holder.gambar.setImageResource(data.gambarThumbnailResId);
        holder.tagUmur.setText(data.umur);
        holder.tagGender.setText(data.gender);

        // +++ Modifikasi OnClickListener untuk item +++
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                // Panggil method di listener (yang diimplementasikan di Fragment)
                clickListener.onHewanClick(data);
            }
        });
        // ++++++++++++++++++++++++++++++++++++++++++++++
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
            nama = itemView.findViewById(R.id.txtNama);
            lokasi = itemView.findViewById(R.id.txtLokasi);
            tagUmur = itemView.findViewById(R.id.tagUmur);
            tagGender = itemView.findViewById(R.id.tagGender);
            gambar = itemView.findViewById(R.id.imgHewan);
        }
    }
}
