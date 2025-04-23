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

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class); // Gunakan DoctorViewModel

        // --- Kode dari onCreate DoctorActivity ---

        // Setup tombol back (jika tidak pakai Toolbar AppActivity)
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup RecyclerView
        setupRecyclerView();

        // Observe LiveData dari ViewModel
        viewModel.salonList.observe(getViewLifecycleOwner(), vetClinics -> { // Ganti nama variabel jika perlu
            if (vetClinics != null) {
                adapter.updateData(vetClinics); // Update adapter saat data berubah
            }
        });

        // Setup tombol filter kategori
        String[] kategoriList = {"Semua", "Vaccine", "GCU", "Medicine", "Check-up", "Emergency"}; // Kategori untuk dokter
        setupCategoryButtons(binding.kategoriContainer, kategoriList);

        // --- Akhir kode dari onCreate DoctorActivity ---
    }

    private void setupRecyclerView() {
        binding.recyclerVet.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Buat adapter dengan listener klik, berikan tipe "doctor"
        adapter = new SalonAdapter(requireContext(), new ArrayList<>(), "doctor", (salon, tipe) -> { // Terima tipe juga
            // Handle klik item vet -> Navigasi ke DetailVetFragment
            try {
                // Pastikan action dan argumen (jika ada) sudah didefinisikan di nav_graph.xml
                DoctorFragmentDirections.ActionDoctorFragmentToDetailVetFragment action =
                        DoctorFragmentDirections.actionDoctorFragmentToDetailVetFragment(); // Tambahkan argumen jika perlu

                // Contoh jika mengirim ID vet
                // action.setVetId(salon.getId()); // Asumsi Salon punya getId()

                // Contoh jika mengirim nama vet
                // action.setNamaVet(salon.nama);

                NavHostFragment.findNavController(DoctorFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Detail Vet belum siap.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerVet.setAdapter(adapter);
    }

    // Fungsi untuk setup tombol kategori (sama seperti di GroomingFragment)
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
                viewModel.loadSalons(kategori); // Panggil method ViewModel (loadSalons atau nama lain yang sesuai)
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
