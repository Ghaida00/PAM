package com.example.projectakhir.ui.grooming;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.projectakhir.databinding.FragmentDoctorBinding; // Nama binding

import java.util.ArrayList;

public class DoctorFragment extends Fragment {

    private FragmentDoctorBinding binding; // View Binding
    private SalonAdapter adapter;
    private DoctorViewModel viewModel; // Gunakan DoctorViewModel

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);

        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        setupRecyclerView();
        observeViewModel();

        String[] kategoriList = {"Semua", "Vaccine", "GCU", "Medicine", "Check-up", "Emergency"}; // Kategori untuk dokter
        setupCategoryButtons(binding.kategoriContainer, kategoriList);
    }

    private void setupRecyclerView() {
        binding.recyclerVet.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SalonAdapter(requireContext(), new ArrayList<>(), "doctor", (salon, tipe) -> { //
            if (salon.getId() == null || salon.getId().isEmpty()) {
                Toast.makeText(requireContext(), "Error: ID Klinik tidak ditemukan.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // Navigasi ke detail fragment dengan ID unik dari Firestore
                DoctorFragmentDirections.ActionDoctorFragmentToDetailVetFragment action =
                        DoctorFragmentDirections.actionDoctorFragmentToDetailVetFragment(salon.getId());
                NavHostFragment.findNavController(DoctorFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Vet belum siap.", Toast.LENGTH_SHORT).show(); //
            }
        });
        binding.recyclerVet.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Mengamati perubahan pada daftar klinik
        viewModel.vetList.observe(getViewLifecycleOwner(), vetClinics -> {
            if (vetClinics != null) {
                adapter.updateData(vetClinics);
            }
        });

        // Mengamati status loading
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            ProgressBar progressBar = requireView().findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            binding.recyclerVet.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        // Mengamati pesan error
        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }


    // Fungsi untuk setup tombol kategori (sama seperti di GroomingFragment)
    private void setupCategoryButtons(LinearLayout container, String[] categories) {
        container.removeAllViews();
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
                viewModel.loadVets(kategori); // Panggil method ViewModel (loadSalons atau nama lain yang sesuai)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
