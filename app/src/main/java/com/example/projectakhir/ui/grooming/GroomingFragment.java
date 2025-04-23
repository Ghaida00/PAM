package com.example.projectakhir.ui.grooming;

import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.SalonAdapter; // Import SalonAdapter
import com.example.projectakhir.data.Salon; // Import data Salon
import com.example.projectakhir.databinding.FragmentGroomingBinding; // Nama binding

import java.util.ArrayList;

public class GroomingFragment extends Fragment {

    private FragmentGroomingBinding binding; // View Binding
    private SalonAdapter adapter;
    private GroomingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGroomingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi ViewModel
        // Gunakan requireActivity() jika ingin ViewModel shared dengan Activity/Fragment lain
        viewModel = new ViewModelProvider(this).get(GroomingViewModel.class);

        // --- Kode dari onCreate GroomingActivity ---

        // Setup tombol back (jika tidak pakai Toolbar AppActivity)
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup RecyclerView
        setupRecyclerView();

        // Observe LiveData dari ViewModel
        viewModel.salonList.observe(getViewLifecycleOwner(), salons -> {
            if (salons != null) {
                adapter.updateData(salons); // Update adapter saat data berubah
            }
        });

        // Setup tombol filter kategori
        String[] kategoriList = {"Semua", "Wash", "Brush", "Spa", "Cut"}; // Kategori untuk grooming
        setupCategoryButtons(binding.kategoriContainer, kategoriList);

        // --- Akhir kode dari onCreate GroomingActivity ---

        // Tidak perlu setup EdgeToEdge atau WindowInsetsListener di Fragment,
        // biasanya ditangani oleh Activity induk (AppActivity)
    }

    private void setupRecyclerView() {
        binding.recyclerSalon.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Buat adapter dengan listener klik, berikan tipe "grooming"
        adapter = new SalonAdapter(requireContext(), new ArrayList<>(), "grooming", (salon, tipe) -> {
            // Handle klik item salon -> Navigasi ke DetailSalonFragment
            try {
                // Pastikan action dan argumen (jika ada) sudah didefinisikan di nav_graph.xml
                GroomingFragmentDirections.ActionGroomingFragmentToDetailSalonFragment action =
                        GroomingFragmentDirections.actionGroomingFragmentToDetailSalonFragment(); // Tambahkan argumen jika perlu

                // Contoh jika mengirim ID salon (perlu argumen di nav_graph & DetailSalonFragment)
                // action.setSalonId(salon.getId()); // Asumsi Salon punya getId()

                // Contoh jika mengirim nama salon (perlu argumen di nav_graph & DetailSalonFragment)
                // action.setNamaSalon(salon.nama);

                NavHostFragment.findNavController(GroomingFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Salon belum siap.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerSalon.setAdapter(adapter);
    }

    // Fungsi untuk setup tombol kategori (sama seperti di DaftarHewanFragment)
    private void setupCategoryButtons(LinearLayout container, String[] categories) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (String kategori : categories) {
            TextView txt = new TextView(requireContext());
            txt.setText(kategori);
            txt.setTextSize(14f);
            txt.setPadding(32, 16, 32, 16);
            txt.setBackgroundResource(R.drawable.bg_tag_kuning); // Default non-selected
            txt.setTextColor(Color.BLACK);
            txt.setTypeface(null, Typeface.BOLD);
            txt.setGravity(Gravity.CENTER);
            txt.setClickable(true);
            txt.setFocusable(true);
            // foreground ripple
            // ...

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0); // Margin kanan
            txt.setLayoutParams(lp);

            txt.setOnClickListener(v -> {
                // Reset background semua tag
                for (int i = 0; i < container.getChildCount(); i++) {
                    View child = container.getChildAt(i);
                    child.setBackgroundResource(R.drawable.bg_tag_kuning);
                    if (child instanceof TextView) {
                        ((TextView) child).setTextColor(Color.BLACK);
                    }
                }
                // Set background tag yang diklik
                txt.setBackgroundResource(R.drawable.bg_tag_hijau);
                txt.setTextColor(Color.WHITE);

                // Filter data melalui ViewModel
                viewModel.loadSalons(kategori); // Panggil method ViewModel
            });
            container.addView(txt);
        }
        // Set tag "Semua" sebagai default selected
        if (container.getChildCount() > 0) {
            View firstChild = container.getChildAt(0);
            firstChild.setBackgroundResource(R.drawable.bg_tag_hijau);
            if (firstChild instanceof TextView) {
                ((TextView) firstChild).setTextColor(Color.WHITE);
            }
        }
    }

    // Anda perlu memodifikasi SalonAdapter untuk menerima listener
    // (Lihat kode SalonAdapter di bawah)

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
