package com.example.projectakhir.ui.adoption;

// Import yang diperlukan
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
import android.widget.Toast;
import android.widget.ProgressBar; // Import ProgressBar

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide; // Import Glide
import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentAdoptHomeBinding;

import java.util.ArrayList; // Untuk list kosong
import java.util.List;     // Untuk tipe data list


public class AdoptHomeFragment extends Fragment {

    private FragmentAdoptHomeBinding binding;
    private AdoptHomeViewModel adoptHomeViewModel; // ViewModel

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdoptHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi ViewModel
        adoptHomeViewModel = new ViewModelProvider(this).get(AdoptHomeViewModel.class);

        // Setup Observers
        // observeUserProfile(); // Observe user data
        observeNewestHewan(); // Observe newest animal data

        // Setup Listener Navigasi (tetap sama)
        binding.cardFormPengaduan.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_adoptHomeFragment_to_formPengaduanFragment);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Form Pengaduan belum siap.", Toast.LENGTH_SHORT).show();
            }
        });


        // --- Implementasi Navigasi Header Profile Picture ---
        /*binding.ivHeaderUserProfile.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(this).navigate(R.id.action_adoptHomeFragment_to_profileFragment);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Profil belum siap.", Toast.LENGTH_SHORT).show();
            }
        });*/
        // --- Akhir Implementasi Navigasi Header Profile Picture ---

        binding.cardProgresAdopsi.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_adoptHomeFragment_to_progresMainFragment);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Progres belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        setupGridKota(); // Grid kota bisa tetap statis atau dinamis dari Firestore nanti
    }


    private void observeNewestHewan() {
        // Observe LiveData dari ViewModel untuk hewan terbaru
        adoptHomeViewModel.newestHewanList.observe(getViewLifecycleOwner(), hewans -> {
            if (hewans != null) {
                setupHewanBaruList(hewans); // Panggil method untuk menampilkan hewan
            }
        });

        adoptHomeViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                if (binding.progressBarNewestHewan != null) {
                    binding.progressBarNewestHewan.setVisibility(View.VISIBLE);
                }
                binding.scrollViewNewestHewan.setVisibility(View.GONE);
            } else {
                if (binding.progressBarNewestHewan != null) {
                    binding.progressBarNewestHewan.setVisibility(View.GONE);
                }
                binding.scrollViewNewestHewan.setVisibility(View.VISIBLE);
            }
        });

        adoptHomeViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                if (binding.progressBarNewestHewan != null) {
                    binding.progressBarNewestHewan.setVisibility(View.GONE);
                }
                binding.scrollViewNewestHewan.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                adoptHomeViewModel.clearError();
            }
        });
    }

    private void setupGridKota() {
        // Kode setupGridKota tetap sama, karena ini statis
        binding.gridKota.removeAllViews();
        tambahKota("Jakarta", R.drawable.jakarta, R.drawable.bg_kota_jakarta); //
        tambahKota("Surabaya", R.drawable.surabaya, R.drawable.bg_kota_surabaya); //
        tambahKota("Yogyakarta", R.drawable.yogyakarta, R.drawable.bg_kota_yogyakarta); //
        tambahKota("Bali", R.drawable.bali, R.drawable.bg_kota_bali); //
    }

    // Modifikasi setupHewanBaru menjadi setupHewanBaruList
    private void setupHewanBaruList(List<Hewan> hewanList) {
        binding.listBaru.removeAllViews(); // Bersihkan view lama
        if (hewanList == null || hewanList.isEmpty()) {
            // Opsional: Tampilkan pesan jika tidak ada hewan baru
            TextView noDataMsg = new TextView(requireContext());
            noDataMsg.setText("Belum ada hewan baru.");
            noDataMsg.setPadding(16,16,16,16);
            binding.listBaru.addView(noDataMsg);
            return;
        }
        for (Hewan h : hewanList) {
            tambahHewanBaruCard(h); // Panggil method untuk membuat card per hewan
        }
    }

    // Modifikasi tambahHewanBaru menjadi tambahHewanBaruCard
    private void tambahHewanBaruCard(Hewan h) {
        LinearLayout card = (LinearLayout) LayoutInflater.from(requireContext())
                .inflate(R.layout.item_hewan_baru_card, binding.listBaru, false); // Buat layout XML terpisah jika lebih kompleks

        ImageView imgHewan = card.findViewById(R.id.imgHewanBaru); // Ganti dengan ID dari item_hewan_baru_card.xml
        TextView namaHewan = card.findViewById(R.id.txtNamaHewanBaru); // Ganti dengan ID
        TextView infoHewan = card.findViewById(R.id.txtInfoHewanBaru);   // Ganti dengan ID
        TextView tagUmur = card.findViewById(R.id.tagUmurHewanBaru);     // Ganti dengan ID
        TextView tagGender = card.findViewById(R.id.tagGenderHewanBaru); // Ganti dengan ID

        if (h.getThumbnailImageUrl() != null && !h.getThumbnailImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(h.getThumbnailImageUrl())
                    .placeholder(R.drawable.grace) // Placeholder
                    .error(R.drawable.ic_paw)       // Error image
                    .centerCrop()
                    .into(imgHewan);
        } else {
            imgHewan.setImageResource(R.drawable.grace); // Default jika URL tidak ada
        }

        namaHewan.setText(h.getNama());
        infoHewan.setText(h.getKota() + "  •  " + h.getJenis()); // Sesuaikan info yang ingin ditampilkan
        tagUmur.setText(h.getUmur());
        tagGender.setText(h.getGender());


        card.setOnClickListener(v -> {
            try {
                String hewanIdentifier = h.getId() != null && !h.getId().isEmpty() ? h.getId() : h.getNama();
                AdoptHomeFragmentDirections.ActionAdoptHomeFragmentToDetailHewanFragment action =
                        AdoptHomeFragmentDirections.actionAdoptHomeFragmentToDetailHewanFragment(hewanIdentifier);
                NavHostFragment.findNavController(AdoptHomeFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Hewan belum siap.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.listBaru.addView(card);
    }


    // Metode tambahKota tetap sama
    private void tambahKota(String nama, int iconResId, int bgDrawableResId) {
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
                AdoptHomeFragmentDirections.ActionAdoptHomeFragmentToDaftarHewanFragment action =
                        AdoptHomeFragmentDirections.actionAdoptHomeFragmentToDaftarHewanFragment(nama);
                NavHostFragment.findNavController(AdoptHomeFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Daftar Hewan belum siap.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.gridKota.addView(card);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}