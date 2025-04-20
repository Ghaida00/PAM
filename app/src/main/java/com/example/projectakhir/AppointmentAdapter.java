package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter untuk menampilkan AppointmentData di RecyclerView
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Context context;
    private List<AppointmentData> appointmentList;

    // Constructor
    public AppointmentAdapter(Context context, List<AppointmentData> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_appointment.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get data pada posisi saat ini
        AppointmentData data = appointmentList.get(position);

        // Set data ke views di ViewHolder
        holder.txtNama.setText(data.petName);
        holder.txtWaktu.setText(data.appointmentDay + " • " + data.appointmentTime);
        holder.icon.setImageResource(data.iconRes);

        // Set background berdasarkan hari
        if ("Today".equalsIgnoreCase(data.appointmentDay)) {
            holder.background.setBackgroundResource(R.drawable.bg_tag_kuning); // Pastikan drawable ada
        } else {
            holder.background.setBackgroundResource(R.drawable.bg_tag_hijau); // Pastikan drawable ada
        }

        // Set OnClickListener untuk item
        holder.itemView.setOnClickListener(v -> {
            // Tampilkan Toast
            String toastMessage = data.serviceType + " Appointment untuk " + data.petName;
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();

            // Intent ke Detail Activity
            Intent detailIntent = new Intent(context, AppointmentDetailActivity.class);

            // Kirim SEMUA data detail menggunakan putExtra
            detailIntent.putExtra("serviceType", data.serviceType);
            detailIntent.putExtra("serviceDetails", data.serviceDetails);
            detailIntent.putExtra("providerName", data.providerName);
            detailIntent.putExtra("providerAddress", data.providerAddress);
            detailIntent.putExtra("appointmentDateTime", data.appointmentDay + " - " + data.appointmentTime);
            detailIntent.putExtra("petNameType", data.petName + " - " + data.petType);
            detailIntent.putExtra("petNotes", data.petNotes);
            detailIntent.putExtra("iconRes", data.iconRes);
            // Tambahkan ID atau status jika perlu

            context.startActivity(detailIntent);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    // ViewHolder inner class
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