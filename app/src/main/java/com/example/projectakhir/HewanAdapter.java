package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HewanAdapter extends RecyclerView.Adapter<HewanAdapter.ViewHolder> {

    List<Hewan> list;
    Context context;

    public HewanAdapter(Context context, List<Hewan> list) {
        this.context = context;
        this.list = list;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailHewanActivity.class);
            intent.putExtra("nama", data.nama);
            intent.putExtra("kota", data.kota);
            intent.putExtra("jenis", data.jenis);
            intent.putExtra("umur", data.umur);
            intent.putExtra("gender", data.gender);
            intent.putExtra("berat", data.berat);
            intent.putExtra("gambar", data.gambarDetailResId);
            intent.putExtra("deskripsi", data.deskripsi);
            intent.putExtra("traits", data.traits); // ArrayList<String>
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
