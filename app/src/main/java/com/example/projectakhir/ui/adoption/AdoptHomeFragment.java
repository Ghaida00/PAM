package com.example.projectakhir.ui.adoption;

import android.content.Intent; // Masih dibutuhkan untuk Intent ke DetailHewanActivity/FormPengaduanActivity/ProgresMainActivity sebelum dikonversi
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment; // Penting untuk navigasi

import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan; // Pastikan import data Hewan benar
import com.example.projectakhir.databinding.FragmentAdoptHomeBinding; // <- Nama class binding (sesuaikan jika nama layout berbeda)
// Hapus import yang tidak perlu seperti AppCompatActivity
// Hapus import HeartActivity, ProgresMainActivity, FormPengaduanActivity jika navigasi sudah pakai NavController

public class AdoptHomeFragment extends Fragment {

    // Gunakan ViewBinding untuk mengakses view
    private FragmentAdoptHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout menggunakan ViewBinding
        binding = FragmentAdoptHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate AdoptHomeActivity dipindahkan ke sini ---

        // Setup Listener untuk Navigasi menggunakan NavController
        binding.cardFormPengaduan.setOnClickListener(v -> {
            // Navigasi ke FormPengaduanFragment (Asumsi action sudah dibuat di nav_graph.xml)
            try {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_adoptHomeFragment_to_formPengaduanFragment); // Ganti ID action jika berbeda
            } catch (IllegalArgumentException e) {
                // Tangani error jika action belum didefinisikan atau fragment tidak ditemukan
                // Misalnya, tampilkan Toast atau log error
                android.widget.Toast.makeText(requireContext(), "Navigasi ke Form Pengaduan belum siap.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        binding.cardProgres.setOnClickListener(v -> {
            // Navigasi ke ProgresMainFragment (Asumsi action sudah dibuat di nav_graph.xml)
            try {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_adoptHomeFragment_to_progresMainFragment); // Ganti ID action jika berbeda
            } catch (IllegalArgumentException e) {
                android.widget.Toast.makeText(requireContext(), "Navigasi ke Progres belum siap.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Hapus listener untuk navHeart karena BottomNav ditangani AppActivity
        // binding.navHeart.setOnClickListener(v -> { ... });

        // Panggil fungsi untuk mengisi Grid Kota dan List Hewan Baru
        setupGridKota();
        setupHewanBaru();

        // --- Akhir kode dari onCreate AdoptHomeActivity ---
    }

    // Fungsi helper dipindahkan ke dalam Fragment
    private void setupGridKota() {
        // Pastikan gridKota diakses melalui binding
        binding.gridKota.removeAllViews(); // Bersihkan dulu jika dipanggil ulang
        tambahKota("Jakarta", R.drawable.jakarta, R.drawable.bg_kota_jakarta);
        tambahKota("Surabaya", R.drawable.surabaya, R.drawable.bg_kota_surabaya);
        tambahKota("Yogyakarta", R.drawable.yogyakarta, R.drawable.bg_kota_yogyakarta);
        tambahKota("Bali", R.drawable.bali, R.drawable.bg_kota_bali);
    }

    private void setupHewanBaru() {
        // Pastikan listBaru diakses melalui binding
        binding.listBaru.removeAllViews(); // Bersihkan dulu
        // Data dummy, sebaiknya diambil dari ViewModel/Repository nanti
        tambahHewanBaru(new Hewan("Grace", "Surabaya", "Kucing", "2 Tahun", "Betina", "4kg",
                new String[]{"Spoiled", "Lazy", "Smart"}, R.drawable.grace, R.drawable.grace_no_background,
                "Grace manja parah. Suka nempel terus kayak perangko, tapi juga pinter buka lemari sendiri kalau laper."));

        tambahHewanBaru(new Hewan("Claire", "Jakarta", "Anjing", "4 Tahun", "Jantan", "4.6kg",
                new String[]{"Smart", "Fearful", "Lazy"}, R.drawable.claire, R.drawable.claire_no_background,
                "Claire agak penakut sama suara keras, tapi sekali kenal kamu, dia bakal lengket banget. Suka duduk di kaki orang dan ngikutin ke mana pun."));
        // Tambahkan hewan lain jika perlu
    }


    // Fungsi tambahKota dipindahkan ke sini
    private void tambahKota(String nama, int iconResId, int bgDrawableResId) {
        // Gunakan requireContext() untuk mendapatkan Context
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setBackgroundResource(bgDrawableResId);
        card.setGravity(Gravity.CENTER); // Gravity untuk isi card
        card.setElevation(8f);
        card.setClickable(true);
        card.setFocusable(true);
        // Tambahkan foreground untuk efek ripple (opsional)
        // TypedValue outValue = new TypedValue();
        // requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        // card.setForeground(ContextCompat.getDrawable(requireContext(), outValue.resourceId));


        ImageView icon = new ImageView(requireContext());
        icon.setImageResource(iconResId);
        icon.setLayoutParams(new LinearLayout.LayoutParams(96, 96)); // Ukuran ikon

        TextView label = new TextView(requireContext());
        label.setText(nama);
        label.setTextSize(16f);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER); // Gravity untuk teks
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.topMargin = 8; // Beri jarak antara ikon dan label
        label.setLayoutParams(labelParams);


        card.addView(icon);
        card.addView(label);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (metrics.widthPixels * 0.42); // Ukuran card dibuat mendekati persegi

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        // Gunakan margin dari GridLayout jika useDefaultMargins=true, atau set manual
        params.setMargins(16, 16, 16, 16); // Margin antar card
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Agar mengisi kolom secara merata
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);     // Agar mengisi baris (jika diperlukan)
        card.setLayoutParams(params);


        // Navigasi saat card diklik
        card.setOnClickListener(v -> {
            // Gunakan NavController untuk pindah ke DaftarHewanFragment
            // Pastikan action dan argument sudah didefinisikan di nav_graph.xml
            try {
                AdoptHomeFragmentDirections.ActionAdoptHomeFragmentToDaftarHewanFragment action =
                        AdoptHomeFragmentDirections.actionAdoptHomeFragmentToDaftarHewanFragment(nama); // 'nama' adalah nama kota
                NavHostFragment.findNavController(AdoptHomeFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                android.widget.Toast.makeText(requireContext(), "Navigasi ke Daftar Hewan belum siap.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Akses gridKota melalui binding
        binding.gridKota.addView(card);
    }

    // Fungsi tambahHewanBaru dipindahkan ke sini
    private void tambahHewanBaru(Hewan h) {
        // Gunakan requireContext()
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_hewan_rounded);
        card.setElevation(8f);
        card.setPadding(0, 0, 0, 16); // Padding bawah saja agar tag tidak terlalu mepet
        card.setClickable(true);
        card.setFocusable(true);
        // foreground ripple
        // ... (sama seperti di tambahKota)

        ImageView img = new ImageView(requireContext());
        img.setImageResource(h.gambarThumbnailResId);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // Buat drawable terpisah untuk corner radius gambar jika perlu atau set ClipToOutline
        img.setBackgroundResource(R.drawable.bg_hewan_rounded); // Sementara pakai background card
        img.setClipToOutline(true); // Butuh API 21+

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.hewan_baru_image_height) // Definisikan dimensi di dimens.xml jika perlu
                // 380 // Ganti hardcoded pixel dengan dp atau referensi dimensi
        );
        img.setLayoutParams(imgParams);


        TextView namaHewan = new TextView(requireContext());
        namaHewan.setText(h.nama);
        namaHewan.setTextSize(14f);
        namaHewan.setTypeface(null, Typeface.BOLD);
        namaHewan.setPadding(12, 8, 12, 0); // Kurangi padding bawah

        TextView info = new TextView(requireContext());
        info.setText(h.kota + "  •  " + h.gender);
        info.setTextSize(12f);
        info.setPadding(12, 0, 12, 4);

        // Layout untuk tag
        LinearLayout tagLayout = new LinearLayout(requireContext());
        tagLayout.setOrientation(LinearLayout.HORIZONTAL);
        tagLayout.setPadding(12, 4, 12, 0); // Kurangi padding bawah

        // TAG 1 - Umur
        TextView tagUmur = new TextView(requireContext());
        tagUmur.setText(h.umur);
        tagUmur.setTextSize(12f);
        tagUmur.setTextColor(Color.BLACK);
        tagUmur.setPadding(24, 8, 24, 8);
        tagUmur.setBackgroundResource(R.drawable.bg_tag_kuning);
        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tagParams.setMarginEnd(8); // Jarak antar tag
        tagUmur.setLayoutParams(tagParams);

        // TAG 2 - Gender
        TextView tagGender = new TextView(requireContext());
        tagGender.setText(h.gender);
        tagGender.setTextSize(12f);
        tagGender.setTextColor(Color.WHITE); // Sesuai desain? Atau hitam?
        tagGender.setPadding(24, 8, 24, 8);
        tagGender.setBackgroundResource(R.drawable.bg_tag_hijau); // Sesuai desain? Atau kuning?


        tagLayout.addView(tagUmur);
        tagLayout.addView(tagGender);

        card.addView(img);
        card.addView(namaHewan);
        card.addView(info);
        card.addView(tagLayout);

        // Klik card -> Navigasi ke DetailHewanFragment
        card.setOnClickListener(v -> {
            try {
                // Pastikan DetailHewanFragment dan action-nya sudah ada di nav_graph
                // Anda perlu menentukan data apa yang akan dikirim sebagai argumen
                // Contoh: mengirim nama hewan saja
                AdoptHomeFragmentDirections.ActionAdoptHomeFragmentToDetailHewanFragment action =
                        AdoptHomeFragmentDirections.actionAdoptHomeFragmentToDetailHewanFragment(h.nama); // Ganti argumen sesuai kebutuhan

                // Jika mengirim seluruh objek Hewan, Hewan perlu Parcelable/Serializable
                // dan definisikan argumen di nav_graph sebagai tipe custom Parcelable/Serializable

                NavHostFragment.findNavController(AdoptHomeFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                android.widget.Toast.makeText(requireContext(), "Navigasi ke Detail Hewan belum siap.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Atur lebar card dan margin
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.hewan_baru_card_width), // Definisikan dimensi di dimens.xml
                // 380, // Ganti hardcoded pixel
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 16, 0); // Margin kanan antar card
        card.setLayoutParams(lp);

        binding.listBaru.addView(card);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Wajib untuk membersihkan referensi binding
    }
}