package com.example.projectakhir.ui.booking;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer; // Import Consumer
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentBookingBinding; // Nama binding
import com.google.android.flexbox.FlexboxLayout; // Import FlexboxLayout

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays; // Import Arrays
import java.util.Calendar;
import java.util.Locale;

public class BookingFragment extends Fragment {

    private FragmentBookingBinding binding; // View Binding
    private String namaProviderDiterima;
    private ArrayList<String> layananYangDipilih = new ArrayList<>();
    private String tanggalDipilih = "";
    private String waktuDipilih = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen
        if (getArguments() != null) {
            try {
                // Menggunakan Safe Args
                namaProviderDiterima = BookingFragmentArgs.fromBundle(getArguments()).getNamaProvider();
                String[] layananArray = BookingFragmentArgs.fromBundle(getArguments()).getLayananDipilih();
                if (layananArray != null) {
                    layananYangDipilih = new ArrayList<>(Arrays.asList(layananArray));
                }
            } catch (IllegalArgumentException e) {
                // Fallback jika Safe Args belum siap
                namaProviderDiterima = getArguments().getString("namaProvider");
                String[] layananArray = getArguments().getStringArray("layananDipilih");
                if (layananArray != null) {
                    layananYangDipilih = new ArrayList<>(Arrays.asList(layananArray));
                }
                if (namaProviderDiterima == null) {
                    handleArgumentError();
                }
            }
        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen booking tidak ditemukan!", Toast.LENGTH_SHORT).show();
        if (NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.bookingFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate BookingActivity ---

        // Set nama provider (salon/vet)
        binding.txtNamaSalon.setText(namaProviderDiterima != null ? namaProviderDiterima : "Nama Provider");

        // Setup tombol back
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup pilihan tanggal
        setupTanggal();

        // Setup pilihan waktu
        setupWaktu();

        // Setup tombol Confirm
        binding.btnConfirm.setOnClickListener(v -> {
            if (tanggalDipilih.isEmpty() || waktuDipilih.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih tanggal dan waktu dulu ya!", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Implementasi logika booking (kirim data ke ViewModel/Repository)
                // Data yang perlu dikirim: namaProviderDiterima, layananYangDipilih, tanggalDipilih, waktuDipilih
                Toast.makeText(requireContext(), "Booking berhasil! 🎉", Toast.LENGTH_LONG).show();

                // Navigasi kembali setelah booking berhasil
                // Bisa kembali ke detail, atau ke halaman utama (HeartFragment)
                NavHostFragment.findNavController(this).popBackStack(R.id.heartFragment, false); // Kembali ke HeartFragment
                // Atau NavHostFragment.findNavController(this).popBackStack(); // Kembali ke layar sebelumnya (Detail)
            }
        });

        // --- Akhir kode dari onCreate BookingActivity ---
    }

    // Fungsi untuk setup tanggal
    private void setupTanggal() {
        binding.tanggalContainer.removeAllViews(); // Clear view lama
        ArrayList<String> tanggalList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfHari = new SimpleDateFormat("EEE", Locale.ENGLISH); // Mon, Tue, etc.
        SimpleDateFormat sdfTanggal = new SimpleDateFormat("d", Locale.ENGLISH); // 1, 2, 3, ...
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String hari = sdfHari.format(calendar.getTime());
            String tanggal = sdfTanggal.format(calendar.getTime());
            tanggalList.add(hari + ", " + tanggal);
        }

        for (String tgl : tanggalList) {
            TextView t = createTag(tgl, binding.tanggalContainer, selected -> {
                tanggalDipilih = selected;
                // Reset pilihan waktu saat tanggal berubah (opsional)
                resetSelection(binding.waktuContainer);
                waktuDipilih = "";
            });
            binding.tanggalContainer.addView(t);
        }
    }

    // Fungsi untuk setup waktu
    private void setupWaktu() {
        binding.waktuContainer.removeAllViews(); // Clear view lama
        // TODO: Idealnya, daftar waktu yang tersedia diambil dari backend/API berdasarkan tanggal yang dipilih
        String[] waktuList = {"09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00"}; // Dummy
        for (String jam : waktuList) {
            TextView j = createTag(jam, binding.waktuContainer, selected -> {
                waktuDipilih = selected;
            });
            binding.waktuContainer.addView(j);
        }
    }

    // Fungsi buat tag interaktif (dipindahkan ke sini)
    private TextView createTag(String text, ViewGroup parent, Consumer<String> onSelect) {
        TextView tag = new TextView(requireContext());
        tag.setText(text);
        tag.setTextSize(14f);
        tag.setPadding(32, 16, 32, 16);
        tag.setBackgroundResource(R.drawable.bg_tag_kuning); // Default non-selected
        tag.setTextColor(Color.BLACK);
        tag.setTypeface(null, Typeface.BOLD);
        tag.setGravity(Gravity.CENTER); // Tengahkan teks
        tag.setClickable(true);
        tag.setFocusable(true);
        // foreground ripple
        // ...

        // Tentukan LayoutParams berdasarkan parent
        ViewGroup.MarginLayoutParams params;
        if (parent instanceof FlexboxLayout) {
            params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        } else { // Asumsi LinearLayout atau lainnya
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        params.setMargins(8, 8, 8, 8); // Beri margin di semua sisi
        tag.setLayoutParams(params);

        tag.setOnClickListener(v -> {
            // Reset background semua tag di parent yang sama
            resetSelection(parent);
            // Aktifkan yang dipilih
            tag.setBackgroundResource(R.drawable.bg_tag_hijau); // Selected state
            tag.setTextColor(Color.WHITE);
            // Panggil callback dengan teks yang dipilih
            onSelect.accept(text);
        });

        return tag;
    }

    // Fungsi helper untuk mereset pilihan di container
    private void resetSelection(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView) {
                child.setBackgroundResource(R.drawable.bg_tag_kuning); // Non-selected state
                ((TextView) child).setTextColor(Color.BLACK);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
