package com.example.projectakhir.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan; // <-- Menggunakan model data Hewan
import java.util.ArrayList;
import java.util.List;

public class YourPetAdapter extends RecyclerView.Adapter<YourPetAdapter.ViewHolder> {

    private final Context context;
    private List<Hewan> petList; // <-- Tipe data diubah menjadi Hewan

    // Interface untuk menangani klik (opsional, untuk navigasi nanti)
    public interface OnPetClickListener {
        void onPetClick(Hewan pet);
    }
    private final OnPetClickListener clickListener;

    public YourPetAdapter(Context context, List<Hewan> petList, OnPetClickListener listener) {
        this.context = context;
        this.petList = new ArrayList<>(petList);
        this.clickListener = listener;
    }

    // Metode untuk memperbarui data di adapter
    public void updateData(List<Hewan> newList) {
        this.petList.clear();
        if (newList != null) {
            this.petList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item yang sudah kita buat sebelumnya
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hewan pet = petList.get(position); // <-- Bekerja dengan objek Hewan

        // Mengatur teks sesuai dengan data dari objek Hewan
        String info = pet.getUmur() + "\n" + pet.getNama();
        holder.petInfo.setText(info);

        // Menggunakan Glide untuk memuat gambar
        Glide.with(context)
                .load(pet.getThumbnailImageUrl()) // <-- Mengambil URL gambar dari model Hewan
                .placeholder(R.drawable.grace)
                .error(R.drawable.ic_paw)
                .centerCrop()
                .into(holder.petImage);

        // Menetapkan listener ke seluruh item view dan tombol
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPetClick(pet);
            }
        });

        holder.seeButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPetClick(pet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petList != null ? petList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView petImage;
        TextView petInfo;
        Button seeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.imgUserPet);
            petInfo = itemView.findViewById(R.id.txtUserPetInfo);
            seeButton = itemView.findViewById(R.id.btnSeePet);
        }
    }
}