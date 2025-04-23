package com.example.projectakhir.ui.grooming;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity; // Import Gravity
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;
import android.widget.Toast; // Untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon; // Jika perlu model Salon (atau Vet)
import com.example.projectakhir.databinding.FragmentDetailVetBinding; // Nama binding

import java.util.ArrayList;

public class DetailVetFragment extends Fragment {

    private FragmentDetailVetBinding binding; // View Binding

    // Variabel untuk data vet (idealnya dari ViewModel berdasarkan ID)
    private String nama;
    private String kota;
    private String jam;
    private int gambarResId;
    private ArrayList<String> layananTersedia = new ArrayList<>(); // Layanan yang ditawarkan vet
    private ArrayList<String> layananDipilih = new ArrayList<>(); // Layanan yang dipilih user
    // private String jenisHewanDipilih = "Dog"; // Jika pilihan jenis hewan masih relevan

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen (jika ada)
        if (getArguments() != null) {
            // Contoh jika mengirim nama vet via Safe Args
            // nama = DetailVetFragmentArgs.fromBundle(getArguments()).getNamaVet();

            // TODO: Idealnya, dapatkan ID vet dari argumen, lalu minta detail lengkap
            // dari ViewModel/Repository.
            // loadVetDetails(vetId);
        }

        // --- Data Dummy Sementara (GANTI DENGAN DATA AKTUAL DARI ARGUMEN/VIEWMODEL) ---
        nama = getArguments() != null ? getArguments().getString("namaVet", "Nama Vet (Dummy)") : "Nama Vet (Dummy)";
        kota = "Surabaya (Dummy)";
        jam = "08:00 - 19:00 (Dummy)";
        gambarResId = R.drawable.anomali; // Gambar dummy
        layananTersedia.clear();
        layananTersedia.add("Vaccine");
        layananTersedia.add("GCU");
        layananTersedia.add("Medicine");
        layananTersedia.add("Check-up");
        layananTersedia.add("Emergency");
        // --- Akhir Data Dummy ---
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailVetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate DetailVetActivity ---

        // Setup tombol back
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Tampilkan data vet (gunakan data yang sudah diambil/dummy)
        binding.imgVet.setImageResource(gambarResId);
        binding.namaVet.setText(nama);
        binding.kotaVet.setText(kota);
        binding.descVet.setText("Klinik hewan terpercaya untuk kesehatan anakbulumu. Pemeriksaan dilakukan oleh dokter berpengalaman dengan penanganan penuh kasih. Karena mereka pantas mendapatkan perawatan terbaik. Buka: " + jam);

        // Setup pilihan jenis hewan (jika masih diperlukan)
        setupJenisHewanSelection(); // Anda bisa membuat fungsi ini

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

        // --- Akhir kode dari onCreate DetailVetActivity ---
    }

    // Fungsi untuk setup pilihan jenis hewan (jika masih ada di desain/kebutuhan)
    private void setupJenisHewanSelection() {
        // Contoh sederhana menggunakan TextView yang bisa diklik
        View.OnClickListener jenisHewanListener = v -> {
            // Reset background semua pilihan
            binding.txtJenisDog.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisDog.setTextColor(Color.BLACK);
            binding.txtJenisCat.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisCat.setTextColor(Color.BLACK);
            binding.txtJenisOthers.setBackgroundResource(R.drawable.bg_tag_kuning);
            binding.txtJenisOthers.setTextColor(Color.BLACK);

            // Set background yang dipilih
            v.setBackgroundResource(R.drawable.bg_tag_hijau); // Warna terpilih
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(Color.WHITE); // Warna teks terpilih
                // Simpan jenis hewan yang dipilih jika perlu
                // jenisHewanDipilih = ((TextView) v).getText().toString();
            }
        };

        binding.txtJenisDog.setOnClickListener(jenisHewanListener);
        binding.txtJenisCat.setOnClickListener(jenisHewanListener);
        binding.txtJenisOthers.setOnClickListener(jenisHewanListener);

        // Set default selection (misal Dog)
        binding.txtJenisDog.setBackgroundResource(R.drawable.bg_tag_hijau);
        binding.txtJenisDog.setTextColor(Color.WHITE);
    }

    // Fungsi untuk membuat tag layanan (mirip DetailSalonFragment)
    private void setupLayananTags() {
        binding.layananContainer.removeAllViews(); // Hapus view lama
        layananDipilih.clear(); // Reset pilihan

        for (String l : layananTersedia) {
            TextView tag = new TextView(requireContext());
            tag.setText(l);
            tag.setTextSize(12f);
            tag.setPadding(24, 12, 24, 12);
            tag.setTextColor(Color.BLACK);
            tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Default: tidak dipilih
            tag.setGravity(Gravity.CENTER);
            tag.setClickable(true);
            tag.setFocusable(true);

            // Menggunakan LayoutParams dari FlexboxLayout jika parentnya FlexboxLayout
            com.google.android.flexbox.FlexboxLayout.LayoutParams lp =
                    new com.google.android.flexbox.FlexboxLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            // Margin antar tag
            lp.setMargins(0, 0, 16, 16); // Margin kanan dan bawah
            tag.setLayoutParams(lp);

            // Listener untuk memilih/batal memilih layanan
            tag.setOnClickListener(v -> {
                if (layananDipilih.contains(l)) {
                    layananDipilih.remove(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_kuning);
                    tag.setTextColor(Color.BLACK);
                } else {
                    layananDipilih.add(l);
                    tag.setBackgroundResource(R.drawable.bg_tag_hijau);
                    tag.setTextColor(Color.WHITE);
                }
            });

            binding.layananContainer.addView(tag);
        }
    }

    // Fungsi untuk navigasi ke BookingFragment
    private void navigateToBooking() {
        try {
            // Pastikan action dan argumen sudah didefinisikan di nav_graph.xml
            DetailVetFragmentDirections.ActionDetailVetFragmentToBookingFragment action =
                    DetailVetFragmentDirections.actionDetailVetFragmentToBookingFragment(nama); // Kirim nama vet

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
