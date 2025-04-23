package com.example.projectakhir.ui.grooming;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon; // Jika perlu model Salon
import com.example.projectakhir.databinding.FragmentSalonDetailBinding; // Nama binding

import java.util.ArrayList;

public class DetailSalonFragment extends Fragment {

    private FragmentSalonDetailBinding binding; // View Binding

    // Variabel untuk data salon (idealnya dari ViewModel berdasarkan ID)
    private String nama;
    private String kota;
    private String jam;
    private int gambarResId;
    private ArrayList<String> layananTersedia = new ArrayList<>(); // Daftar layanan yang ditawarkan salon ini
    private ArrayList<String> layananDipilih = new ArrayList<>(); // Daftar layanan yang dipilih user

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen (jika ada)
        if (getArguments() != null) {
            // Contoh jika mengirim nama salon via Safe Args
            // nama = DetailSalonFragmentArgs.fromBundle(getArguments()).getNamaSalon();

            // TODO: Idealnya, dapatkan ID salon dari argumen, lalu minta detail lengkap
            // salon (termasuk layananTersedia, jam, gambar, kota) dari ViewModel/Repository.
            // loadSalonDetails(salonId);
        }

        // --- Data Dummy Sementara (GANTI DENGAN DATA AKTUAL DARI ARGUMEN/VIEWMODEL) ---
        // Ini HANYA contoh jika belum ada mekanisme pengambilan data detail
        nama = getArguments() != null ? getArguments().getString("namaSalon", "Nama Salon (Dummy)") : "Nama Salon (Dummy)"; // Ambil nama dari argumen jika ada
        kota = "Surabaya (Dummy)";
        jam = "09:00 - 20:00 (Dummy)";
        gambarResId = R.drawable.grace; // Gambar dummy
        layananTersedia.clear();
        layananTersedia.add("Wash");
        layananTersedia.add("Brush");
        layananTersedia.add("Spa");
        layananTersedia.add("Cut");
        // --- Akhir Data Dummy ---
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSalonDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate DetailSalonActivity ---

        // Setup tombol back
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Tampilkan data salon (gunakan data yang sudah diambil/dummy)
        binding.imgSalon.setImageResource(gambarResId);
        binding.namaSalon.setText(nama);
        binding.kotaSalon.setText(kota);
        binding.descSalon.setText("Tempat grooming terpercaya dengan pelayanan " + layananTersedia.size() + " jenis layanan. Jam buka: " + jam);

        // Setup tampilan layanan yang bisa dipilih
        setupLayananTags();

        // Setup tombol Book Now
        binding.btnBook.setOnClickListener(v -> {
            if (layananDipilih.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih minimal satu layanan", Toast.LENGTH_SHORT).show();
            } else {
                navigateToBooking();
            }
        });

        // --- Akhir kode dari onCreate DetailSalonActivity ---
    }

    // Fungsi untuk membuat tag layanan
    private void setupLayananTags() {
        binding.layananContainer.removeAllViews(); // Hapus view lama
        layananDipilih.clear(); // Reset pilihan saat view dibuat ulang

        for (String l : layananTersedia) {
            TextView tag = new TextView(requireContext());
            tag.setText(l);
            tag.setTextSize(12f); // Sesuaikan ukuran
            tag.setPadding(24, 12, 24, 12); // Sesuaikan padding
            tag.setTextColor(Color.BLACK);
            tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Default: tidak dipilih
            tag.setGravity(Gravity.CENTER);
            tag.setClickable(true);
            tag.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // Margin antar tag
            lp.setMargins(0, 0, 16, 16); // Margin kanan dan bawah
            tag.setLayoutParams(lp);

            // Listener untuk memilih/batal memilih layanan
            tag.setOnClickListener(v -> {
                if (layananDipilih.contains(l)) {
                    // Jika sudah dipilih -> batalkan pilihan
                    layananDipilih.remove(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Kembali ke state tidak dipilih
                    tag.setTextColor(Color.BLACK);
                } else {
                    // Jika belum dipilih -> pilih
                    layananDipilih.add(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_hijau); // State dipilih
                    tag.setTextColor(Color.WHITE); // Warna teks saat dipilih
                }
            });

            binding.layananContainer.addView(tag);
        }
    }

    // Fungsi untuk navigasi ke BookingFragment
    private void navigateToBooking() {
        try {
            // Pastikan action dan argumen sudah didefinisikan di nav_graph.xml
            DetailSalonFragmentDirections.ActionDetailSalonFragmentToBookingFragment action =
                    DetailSalonFragmentDirections.actionDetailSalonFragmentToBookingFragment(nama); // Kirim nama salon

            // Kirim layanan yang dipilih sebagai String array
            action.setLayananDipilih(layananDipilih.toArray(new String[0]));

            NavHostFragment.findNavController(this).navigate(action);
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Navigasi ke Booking belum siap.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
