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

import android.widget.ProgressBar;
import android.widget.Toast;
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

        viewModel = new ViewModelProvider(this).get(GroomingViewModel.class);
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        setupRecyclerView();
        observeViewModel();

        String[] kategoriList = {"Semua", "Wash", "Brush", "Spa", "Cut"};
        setupCategoryButtons(binding.kategoriContainer, kategoriList);
    }

    private void setupRecyclerView() {
        binding.recyclerSalon.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SalonAdapter(requireContext(), new ArrayList<>(), "grooming", (salon, tipe) -> {
            if (salon.getId() == null || salon.getId().isEmpty()) {
                Toast.makeText(requireContext(), "Error: ID layanan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Navigasi dengan ID unik
            GroomingFragmentDirections.ActionGroomingFragmentToDetailSalonFragment action =
                    GroomingFragmentDirections.actionGroomingFragmentToDetailSalonFragment(salon.getId());
            NavHostFragment.findNavController(GroomingFragment.this).navigate(action);
        });
        binding.recyclerSalon.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.salonList.observe(getViewLifecycleOwner(), salons -> {
            adapter.updateData(salons);
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            // Menggunakan ID dari ProgressBar yang ditambahkan di XML
            ProgressBar progressBar = requireView().findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            binding.recyclerSalon.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
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
