package com.example.projectakhir.ui.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProgresMainBinding; // Nama binding

public class ProgresMainFragment extends Fragment {

    private FragmentProgresMainBinding binding; // View Binding

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgresMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate ProgresMainActivity ---

        // Setup tombol back
        binding.btnBackProgresMain.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup tombol lihat progres adopsi
        binding.btnLihatProgresAdopsi.setOnClickListener(v -> {
            try {
                // Navigasi ke ProgresAdopsiFragment (pastikan action ada di nav_graph)
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_progresMainFragment_to_progresAdopsiFragment); // Ganti ID jika perlu
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Progres Adopsi belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup tombol lihat progres pengaduan
        binding.btnLihatProgresPengaduan.setOnClickListener(v -> {
            try {
                // Navigasi ke ProgresPengaduanFragment (pastikan action ada di nav_graph)
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_progresMainFragment_to_progresPengaduanFragment); // Ganti ID jika perlu
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Progres Pengaduan belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Akhir kode dari onCreate ProgresMainActivity ---
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
