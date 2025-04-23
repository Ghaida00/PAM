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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.HewanAdapter;
import com.example.projectakhir.data.Hewan; // Pastikan import Hewan benar
import com.example.projectakhir.databinding.FragmentDaftarHewanBinding; // Nama binding

import java.util.ArrayList;

public class DaftarHewanFragment extends Fragment {

    private FragmentDaftarHewanBinding binding; // View Binding
    private HewanAdapter adapter;
    private DaftarHewanViewModel viewModel;
    private String kotaTerpilih;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen "kota" yang dikirim via NavController
        if (getArguments() != null) {
            // Menggunakan Safe Args (jika sudah di-generate setelah rebuild)
            try {
                kotaTerpilih = DaftarHewanFragmentArgs.fromBundle(getArguments()).getKota();
            } catch (IllegalArgumentException e) {
                // Fallback jika Safe Args belum siap atau ada masalah
                kotaTerpilih = getArguments().getString("kota"); // Ambil manual
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
        // Navigasi kembali jika argumen tidak valid
        if (NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.daftarHewanFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDaftarHewanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(DaftarHewanViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Observe LiveData dari ViewModel
        viewModel.hewanList.observe(getViewLifecycleOwner(), hewanList -> {
            if (hewanList != null) {
                adapter.updateData(hewanList);
            }
        });

        // Load data awal berdasarkan kota yang diterima sebagai argumen
        if (kotaTerpilih != null && !kotaTerpilih.isEmpty()) {
            viewModel.loadHewanForCity(kotaTerpilih);
        } else {
            // Seharusnya sudah ditangani di onCreate, tapi sebagai fallback
            if (NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                    NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.daftarHewanFragment) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        }


        // Setup tombol filter kategori
        String[] kategoriList = {"Semua", "Kucing", "Anjing", "Reptil", "Burung"}; // Sesuaikan kategori
        setupCategoryButtons(binding.kategoriContainer, kategoriList);

        // Setup tombol back (otomatis ditangani oleh NavController jika menggunakan Toolbar AppActivity)
        // Jika tidak pakai Toolbar AppActivity atau perlu custom back:
        // binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void setupRecyclerView() {
        binding.recyclerHewan.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Modifikasi adapter untuk menerima listener klik
        adapter = new HewanAdapter(requireContext(), new ArrayList<>(), hewan -> {
            // Handle klik item hewan -> Navigasi ke DetailHewanFragment
            try {
                // Pastikan action dan argumen sudah didefinisikan di nav_graph.xml
                DaftarHewanFragmentDirections.ActionDaftarHewanFragmentToDetailHewanFragment action =
                        DaftarHewanFragmentDirections.actionDaftarHewanFragmentToDetailHewanFragment(hewan.nama); // Kirim nama/ID hewan

                // Jika ingin mengirim data lain, tambahkan argumen di nav_graph dan set di sini
                // action.setJenisHewan(hewan.jenis); // Contoh

                NavHostFragment.findNavController(DaftarHewanFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Hewan belum siap.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerHewan.setAdapter(adapter);
    }

    // Fungsi untuk setup tombol kategori
    private void setupCategoryButtons(LinearLayout container, String[] categories) {
        container.removeAllViews(); // Hapus view lama jika ada
        LayoutInflater inflater = LayoutInflater.from(requireContext()); // Untuk inflate layout tag jika perlu

        for (String kategori : categories) {
            // Menggunakan TextView biasa seperti di Activity sebelumnya
            TextView txt = new TextView(requireContext());
            txt.setText(kategori);
            txt.setTextSize(14f);
            txt.setPadding(32, 16, 32, 16);
            // Default background (non-selected)
            txt.setBackgroundResource(R.drawable.bg_tag_kuning); // Atau drawable lain untuk non-selected
            txt.setTextColor(Color.BLACK);
            txt.setTypeface(null, Typeface.BOLD);
            txt.setGravity(Gravity.CENTER);
            txt.setClickable(true);
            txt.setFocusable(true);
            // Tambahkan foreground ripple
            // ...

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0); // Margin kanan antar tag
            txt.setLayoutParams(lp);

            txt.setOnClickListener(v -> {
                // Optional: Implementasi visual untuk menandai tag yang aktif
                // Reset background semua tag
                for (int i = 0; i < container.getChildCount(); i++) {
                    View child = container.getChildAt(i);
                    child.setBackgroundResource(R.drawable.bg_tag_kuning); // Non-selected state
                    if (child instanceof TextView) {
                        ((TextView) child).setTextColor(Color.BLACK);
                    }
                }
                // Set background tag yang diklik
                txt.setBackgroundResource(R.drawable.bg_tag_hijau); // Selected state
                txt.setTextColor(Color.WHITE); // Warna teks selected state

                // Filter data melalui ViewModel
                viewModel.filterHewan(kategori);
            });
            container.addView(txt);
        }
        // Set tag "Semua" sebagai default selected saat pertama kali
        if (container.getChildCount() > 0) {
            View firstChild = container.getChildAt(0);
            firstChild.setBackgroundResource(R.drawable.bg_tag_hijau);
            if (firstChild instanceof TextView) {
                ((TextView) firstChild).setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting untuk memory safety
    }
}
