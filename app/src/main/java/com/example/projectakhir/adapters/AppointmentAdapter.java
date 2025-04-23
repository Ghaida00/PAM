package com.example.projectakhir.adapters;

import android.content.Context;
// Hapus import Intent dan Toast jika tidak digunakan lagi di sini
// import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
// import android.widget.Toast; // Hapus jika tidak dipakai lagi

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectakhir.data.AppointmentData;
import com.example.projectakhir.R;
// Hapus import AppointmentDetailActivity jika intent dihapus
// import com.example.projectakhir.ui.booking.AppointmentDetailActivity;

import java.util.List;
import java.util.ArrayList;

// Adapter untuk menampilkan AppointmentData di RecyclerView
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    // +++ Interface untuk menangani klik +++
    public interface OnAppointmentClickListener {
        void onAppointmentClick(AppointmentData appointment);
    }
    // ++++++++++++++++++++++++++++++++++++++

    private Context context;
    private List<AppointmentData> appointmentList;
    private final OnAppointmentClickListener clickListener; // Tambahkan variabel listener

    // +++ Modifikasi Constructor untuk menerima listener +++
    public AppointmentAdapter(Context context, List<AppointmentData> appointmentList, OnAppointmentClickListener listener) {
        this.context = context;
        // Gunakan list baru untuk menghindari modifikasi list asli secara tidak sengaja
        this.appointmentList = new ArrayList<>(appointmentList);
        this.clickListener = listener; // Simpan listener
    }
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_appointment.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    // Metode untuk update data (sudah ada dan bagus)
    public void updateData(List<AppointmentData> newList) {
        this.appointmentList.clear();
        if (newList != null) {
            this.appointmentList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get data pada posisi saat ini
        AppointmentData data = appointmentList.get(position);

        // Set data ke views di ViewHolder
        holder.txtNama.setText(data.getPetName());
        holder.txtWaktu.setText(data.getAppointmentDay() + " • " + data.getAppointmentTime());
        holder.icon.setImageResource(data.getIconRes());

        // Set background berdasarkan hari (pastikan drawable bg_tag_kuning & bg_tag_hijau ada)
        if ("Today".equalsIgnoreCase(data.getAppointmentDay())) {
            holder.background.setBackgroundResource(R.drawable.bg_tag_kuning);
        } else {
            holder.background.setBackgroundResource(R.drawable.bg_tag_hijau);
        }

        // +++ Modifikasi OnClickListener untuk item +++
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                // Panggil method di listener (yang diimplementasikan di Fragment)
                clickListener.onAppointmentClick(data);
            }

            // --- HAPUS Logika Intent Lama ---
            // String toastMessage = data.getServiceType() + " Appointment untuk " + data.getPetName();
            // Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            //
            // Intent detailIntent = new Intent(context, AppointmentDetailActivity.class);
            // detailIntent.putExtra("serviceType", data.getServiceType());
            // // ... putExtra lainnya ...
            // context.startActivity(detailIntent);
            // -------------------------------
        });
        // ++++++++++++++++++++++++++++++++++++++++++++++
    }

    @Override
    public int getItemCount() {
        // Kembalikan ukuran list, pastikan list tidak null
        return appointmentList != null ? appointmentList.size() : 0;
    }

    // ViewHolder inner class (tidak perlu diubah)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView txtNama, txtWaktu;
        LinearLayout background; // Root LinearLayout dari item_appointment.xml

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iconLayanan);
            txtNama = itemView.findViewById(R.id.namaHewan);
            txtWaktu = itemView.findViewById(R.id.jadwal);
            background = itemView.findViewById(R.id.bgAppointment); // ID dari root LinearLayout di item_appointment.xml
        }
    }
}