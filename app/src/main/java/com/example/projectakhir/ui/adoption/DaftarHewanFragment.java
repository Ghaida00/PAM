package com.example.projectakhir.ui.adoption;

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
// Import ProgressBar jika belum ada
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.HewanAdapter;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.databinding.FragmentDaftarHewanBinding; // Nama binding

import java.util.ArrayList;

public class DaftarHewanFragment extends Fragment {

    private FragmentDaftarHewanBinding binding; // View Binding
    private HewanAdapter adapter;
    private DaftarHewanViewModel viewModel;
    private String kotaTerpilih;
    // Tidak perlu ProgressBar di sini karena sudah ada di binding
    // private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                kotaTerpilih = DaftarHewanFragmentArgs.fromBundle(getArguments()).getKota();
            } catch (IllegalArgumentException e) {
                kotaTerpilih = getArguments().getString("kota");
                if (kotaTerpilih == null) {
                    handleArgumentError();
                }
            }
        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen kota tidak ditemukan!", Toast.LENGTH_SHORT).show();
        if (getView() != null && NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.daftarHewanFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDaftarHewanBinding.inflate(inflater, container, false);
        // progressBar = binding.progressBarDaftarHewan; // Inisialisasi ProgressBar dari binding
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DaftarHewanViewModel.class);

        setupRecyclerView();

        // Observe LiveData dari ViewModel
        viewModel.hewanList.observe(getViewLifecycleOwner(), hewanList -> {
            // Sembunyikan ProgressBar karena data sudah diterima (atau gagal)
            // binding.progressBarDaftarHewan.setVisibility(View.GONE); // Kita handle di observer isLoading
            if (hewanList != null && !hewanList.isEmpty()) {
                adapter.updateData(hewanList);
                binding.recyclerHewan.setVisibility(View.VISIBLE);
                // Mungkin ada TextView untuk pesan "tidak ada data" yang perlu disembunyikan
                // binding.txtNoData.setVisibility(View.GONE);
            } else if (hewanList != null && hewanList.isEmpty() && !viewModel.isLoading.getValue()){ // Cek jika list kosong dan tidak sedang loading
                adapter.updateData(new ArrayList<>()); // Kosongkan adapter
                binding.recyclerHewan.setVisibility(View.GONE);
                // Tampilkan pesan "tidak ada data"
                Toast.makeText(getContext(), "Tidak ada hewan yang ditemukan untuk filter ini.", Toast.LENGTH_SHORT).show();
                // binding.txtNoData.setVisibility(View.VISIBLE);
                // binding.txtNoData.setText("Tidak ada hewan ditemukan.");
            }
            // Jika hewanList null, error akan ditangani oleh observer error
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBarDaftarHewan.setVisibility(View.VISIBLE);
                binding.recyclerHewan.setVisibility(View.GONE); // Sembunyikan list saat loading
                // binding.txtNoData.setVisibility(View.GONE);
            } else {
                binding.progressBarDaftarHewan.setVisibility(View.GONE);
                // Visibilitas recyclerHewan diatur oleh observer hewanList
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                // binding.progressBarDaftarHewan.setVisibility(View.GONE); // Pastikan progressbar hilang saat error
                binding.recyclerHewan.setVisibility(View.GONE);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                // Tampilkan pesan error di UI jika perlu, misal di TextView
                // binding.txtNoData.setVisibility(View.VISIBLE);
                // binding.txtNoData.setText(errorMsg);
                viewModel.clearError(); // Bersihkan error setelah ditampilkan agar tidak muncul lagi saat re-orientasi
            }
        });


        if (kotaTerpilih != null && !kotaTerpilih.isEmpty()) {
            viewModel.loadHewanForCity(kotaTerpilih);
        } else {
            if (getView() != null && NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                    NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.daftarHewanFragment) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        }

        String[] kategoriList = {"Semua", "Kucing", "Anjing", "Reptil", "Burung"};
        setupCategoryButtons(binding.kategoriContainer, kategoriList);
    }

    private void setupRecyclerView() {
        binding.recyclerHewan.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HewanAdapter(requireContext(), new ArrayList<>(), hewan -> {
            try {
                // Pastikan action dan argumen sudah didefinisikan di nav_graph.xml
                // Kirim ID hewan (jika tersedia dan unik) atau nama hewan sebagai identifier
                String hewanIdentifier = hewan.getId(); // Prioritaskan ID dari Firestore
                if (hewanIdentifier == null || hewanIdentifier.isEmpty()) {
                    hewanIdentifier = hewan.getNama(); // Fallback ke nama jika ID tidak ada
                }

                DaftarHewanFragmentDirections.ActionDaftarHewanFragmentToDetailHewanFragment action =
                        DaftarHewanFragmentDirections.actionDaftarHewanFragmentToDetailHewanFragment(hewanIdentifier);

                NavHostFragment.findNavController(DaftarHewanFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Hewan belum siap atau argumen salah.", Toast.LENGTH_SHORT).show();
                // Log errornya juga untuk debugging
                // Log.e("DaftarHewanFragment", "Error navigasi: ", e);
            }
        });
        binding.recyclerHewan.setAdapter(adapter);
    }

    private void setupCategoryButtons(LinearLayout container, String[] categories) {
        container.removeAllViews();
        // ... (kode setupCategoryButtons tetap sama) ...
        // Pastikan saat tombol kategori diklik, ia memanggil viewModel.filterHewan(kategori);

        for (String kategori : categories) {
            TextView txt = new TextView(requireContext());
            txt.setText(kategori);
            txt.setTextSize(14f);
            txt.setPadding(32, 16, 32, 16);
            txt.setBackgroundResource(R.drawable.bg_tag_kuning); //
            txt.setTextColor(Color.BLACK);
            txt.setTypeface(null, Typeface.BOLD);
            txt.setGravity(Gravity.CENTER);
            txt.setClickable(true);
            txt.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0);
            txt.setLayoutParams(lp);

            txt.setOnClickListener(v -> {
                for (int i = 0; i < container.getChildCount(); i++) {
                    View child = container.getChildAt(i);
                    child.setBackgroundResource(R.drawable.bg_tag_kuning); //
                    if (child instanceof TextView) {
                        ((TextView) child).setTextColor(Color.BLACK);
                    }
                }
                txt.setBackgroundResource(R.drawable.bg_tag_hijau); //
                txt.setTextColor(Color.WHITE);
                viewModel.filterHewan(kategori); // Panggil filter di ViewModel
            });
            container.addView(txt);
        }
        if (container.getChildCount() > 0) {
            View firstChild = container.getChildAt(0);
            firstChild.setBackgroundResource(R.drawable.bg_tag_hijau); //
            if (firstChild instanceof TextView) {
                ((TextView) firstChild).setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}