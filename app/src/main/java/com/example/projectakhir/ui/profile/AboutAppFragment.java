package com.example.projectakhir.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.databinding.FragmentAboutBinding; // Sesuaikan jika nama file XML-mu berbeda

public class AboutAppFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan ViewBinding untuk inflate layout
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup listener untuk tombol kembali
        setupActionListeners();

        // Catatan: Jika kamu perlu memanipulasi ivAppLogo atau tvAppDescription dari kode,
        // kamu bisa melakukannya di sini. Misalnya:
        // binding.ivAppLogo.setImageResource(R.drawable.another_logo);
        // binding.tvAppDescription.setText("Deskripsi baru dari kode.");
    }

    private void setupActionListeners() {
        // Listener untuk tombol kembali di toolbar
        binding.btnBackAbout.setOnClickListener(v -> {
            // Navigasi kembali ke fragment sebelumnya dalam back stack
            if (NavHostFragment.findNavController(AboutAppFragment.this).popBackStack()) {
                // Berhasil kembali
            } else {
                // Opsional: Fallback jika popBackStack() mengembalikan false
                // (misalnya jika ini adalah start destination atau ada masalah navigasi)
                // requireActivity().finish(); // Atau tangani sesuai kebutuhan
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Penting untuk menghapus referensi binding agar tidak terjadi memory leak
        binding = null;
    }
}