package com.example.projectakhir.ui.adoption;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Ditambahkan untuk feedback

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController; // Import NavController
import androidx.navigation.Navigation; // Import Navigation helper
// NavHostFragment sudah diimport di kode Anda, tidak perlu diubah jika sudah ada
// import androidx.navigation.fragment.NavHostFragment;


import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentAdoptHomeBinding;

public class AdoptHomeFragment extends Fragment {

    private FragmentAdoptHomeBinding binding;
    private NavController navController; // Deklarasi NavController

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdoptHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi NavController
        navController = Navigation.findNavController(view);

        // Listener untuk cardFormPengaduan (dari kode Anda)
        // Pastikan binding.cardFormPengaduan ada di layout fragment_adopt_home.xml
        if (binding.cardFormPengaduan != null) {
            binding.cardFormPengaduan.setOnClickListener(v -> {
                try {
                    // Navigasi ke FormPengaduanFragment
                    navController.navigate(R.id.action_adoptHomeFragment_to_formPengaduanFragment);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Navigasi ke Form Pengaduan belum siap.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Listener untuk cardProgres (dari kode Anda)
        // Pastikan binding.cardProgres ada di layout fragment_adopt_home.xml
        if (binding.cardProgres != null) {
            binding.cardProgres.setOnClickListener(v -> {
                try {
                    // Navigasi ke ProgresMainFragment
                    navController.navigate(R.id.action_adoptHomeFragment_to_progresMainFragment);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Navigasi ke Progres belum siap.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- Implementasi Navigasi Header Profile Picture ---
        // Pastikan fragment_adopt_home.xml sudah diupdate dengan header custom
        // dan ImageView profil pengguna memiliki ID "ivHeaderUserProfile"
        // (Akses view binding.ivHeaderUserProfile mungkin perlu disesuaikan jika header di-include secara terpisah)
        if (binding.ivHeaderUserProfile != null) { // Mengasumsikan ID ini ada di fragment_adopt_home.xml
            binding.ivHeaderUserProfile.setOnClickListener(v -> {
                try {
                    // Navigasi ke ProfileFragment menggunakan action ID yang benar
                    navController.navigate(R.id.action_adoptHomeFragment_to_profileFragment);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Navigasi ke Profil belum siap.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Jika Anda ingin memastikan header ada, Anda bisa tambahkan Toast di sini untuk debug
            // Toast.makeText(requireContext(), "DEBUG: ivHeaderUserProfile tidak ditemukan.", Toast.LENGTH_LONG).show();
        }
        // --- Akhir Implementasi Navigasi Header Profile Picture ---


        // Panggil fungsi untuk mengisi Grid Kota dan List Hewan Baru
        setupGridKota();
        setupHewanBaru();
    }

    // Fungsi helper setupGridKota (dari kode Anda)
    private void setupGridKota() {
        if (binding.gridKota == null || getContext() == null) return;
        binding.gridKota.removeAllViews();
        tambahKota("Jakarta", R.drawable.jakarta, R.drawable.bg_kota_jakarta);
        tambahKota("Surabaya", R.drawable.surabaya, R.drawable.bg_kota_surabaya);
        tambahKota("Yogyakarta", R.drawable.yogyakarta, R.drawable.bg_kota_yogyakarta);
        tambahKota("Bali", R.drawable.bali, R.drawable.bg_kota_bali);
    }

    // Fungsi helper setupHewanBaru (dari kode Anda)
    private void setupHewanBaru() {
        if (binding.listBaru == null || getContext() == null) return;
        binding.listBaru.removeAllViews();
        tambahHewanBaru(new Hewan("Grace", "Surabaya", "Kucing", "2 Tahun", "Betina", "4kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.grace, R.drawable.grace_no_background,
                "Grace manja parah. Suka nempel terus kayak perangko, tapi juga pinter buka lemari sendiri kalau laper."));
        tambahHewanBaru(new Hewan("Claire", "Jakarta", "Anjing", "4 Tahun", "Jantan", "4.6kg",
                new String[]{"Smart", "Fearful", "Lazy"}, R.drawable.claire, R.drawable.claire_no_background,
                "Claire agak penakut sama suara keras, tapi sekali kenal kamu, dia bakal lengket banget. Suka duduk di kaki orang dan ngikutin ke mana pun."));
    }


    // Fungsi tambahKota (dari kode Anda, dengan penyesuaian navigasi jika perlu)
    private void tambahKota(String nama, int iconResId, int bgDrawableResId) {
        if (getContext() == null || binding.gridKota == null) return;

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setBackgroundResource(bgDrawableResId);
        card.setGravity(Gravity.CENTER);
        card.setElevation(8f);
        card.setClickable(true);
        card.setFocusable(true);

        ImageView icon = new ImageView(requireContext());
        icon.setImageResource(iconResId);
        icon.setLayoutParams(new LinearLayout.LayoutParams(96, 96));

        TextView label = new TextView(requireContext());
        label.setText(nama);
        label.setTextSize(16f);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.topMargin = 8;
        label.setLayoutParams(labelParams);

        card.addView(icon);
        card.addView(label);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (metrics.widthPixels * 0.42);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(16, 16, 16, 16);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        card.setLayoutParams(params);

        card.setOnClickListener(v -> {
            try {
                // Menggunakan Directions class dari kode Anda, pastikan ini sesuai dengan nav_graph.xml yang baru
                AdoptHomeFragmentDirections.ActionAdoptHomeFragmentToDaftarHewanFragment action =
                        AdoptHomeFragmentDirections.actionAdoptHomeFragmentToDaftarHewanFragment(nama);
                navController.navigate(action);
            } catch (Exception e) { // Lebih baik tangkap Exception spesifik jika mungkin
                Toast.makeText(requireContext(), "Navigasi ke Daftar Hewan (kota) belum siap.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.gridKota.addView(card);
    }

    // Fungsi tambahHewanBaru (dari kode Anda, dengan penyesuaian navigasi jika perlu)
    private void tambahHewanBaru(Hewan h) {
        if (getContext() == null || binding.listBaru == null) return;

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_hewan_rounded);
        card.setElevation(8f);
        card.setPadding(0, 0, 0, 16);
        card.setClickable(true);
        card.setFocusable(true);

        ImageView img = new ImageView(requireContext());
        // Pastikan field seperti gambarThumbnailResId ada di class Hewan Anda
        img.setImageResource(h.gambarThumbnailResId);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setBackgroundResource(R.drawable.bg_hewan_rounded);
        img.setClipToOutline(true);

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.hewan_baru_image_height)
        );
        img.setLayoutParams(imgParams);

        TextView namaHewan = new TextView(requireContext());
        namaHewan.setText(h.nama);
        namaHewan.setTextSize(14f);
        namaHewan.setTypeface(null, Typeface.BOLD);
        namaHewan.setPadding(12, 8, 12, 0);

        TextView info = new TextView(requireContext());
        info.setText(h.kota + "  •  " + h.gender);
        info.setTextSize(12f);
        info.setPadding(12, 0, 12, 4);

        LinearLayout tagLayout = new LinearLayout(requireContext());
        tagLayout.setOrientation(LinearLayout.HORIZONTAL);
        tagLayout.setPadding(12, 4, 12, 0);

        TextView tagUmur = new TextView(requireContext());
        tagUmur.setText(h.umur);
        tagUmur.setTextSize(12f);
        tagUmur.setTextColor(Color.BLACK);
        tagUmur.setPadding(24, 8, 24, 8);
        tagUmur.setBackgroundResource(R.drawable.bg_tag_kuning);
        LinearLayout.LayoutParams tagParamsUmur = new LinearLayout.LayoutParams( // Ganti nama variabel agar unik
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tagParamsUmur.setMarginEnd(8);
        tagUmur.setLayoutParams(tagParamsUmur);

        TextView tagGender = new TextView(requireContext());
        tagGender.setText(h.gender);
        tagGender.setTextSize(12f);
        tagGender.setTextColor(Color.WHITE);
        tagGender.setPadding(24, 8, 24, 8);
        tagGender.setBackgroundResource(R.drawable.bg_tag_hijau);
        // Jika Anda ingin tagGender juga memiliki margin, tambahkan layout params untuknya
        // LinearLayout.LayoutParams tagParamsGender = new LinearLayout.LayoutParams(
        //         LinearLayout.LayoutParams.WRAP_CONTENT,
        //         LinearLayout.LayoutParams.WRAP_CONTENT
        // );
        // tagGender.setLayoutParams(tagParamsGender);


        tagLayout.addView(tagUmur);
        tagLayout.addView(tagGender);

        card.addView(img);
        card.addView(namaHewan);
        card.addView(info);
        card.addView(tagLayout);

        card.setOnClickListener(v -> {
            try {
                // Menggunakan Directions class dari kode Anda
                AdoptHomeFragmentDirections.ActionAdoptHomeFragmentToDetailHewanFragment action =
                        AdoptHomeFragmentDirections.actionAdoptHomeFragmentToDetailHewanFragment(h.nama);
                navController.navigate(action);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Hewan belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.hewan_baru_card_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 16, 0);
        card.setLayoutParams(lp);

        binding.listBaru.addView(card);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}