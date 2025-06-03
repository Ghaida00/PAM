package com.example.projectakhir.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

// Import kelas Binding yang sesuai dengan nama file XML kamu
import com.example.projectakhir.databinding.FragmentDeliveryAddressBinding;

public class DeliveryAddressFragment extends Fragment {

    // Deklarasi variabel binding
    private FragmentDeliveryAddressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout menggunakan ViewBinding
        binding = FragmentDeliveryAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup listener untuk aksi-aksi di fragment ini
        setupActionListeners();

        // Kamu bisa memanipulasi view lain di sini jika perlu, contoh:
        // binding.tvInProgressMessage.setText("Fitur Alamat Pengiriman akan segera hadir!");
    }

    private void setupActionListeners() {
        // Listener untuk tombol kembali di toolbar
        binding.btnBackDelivery.setOnClickListener(v -> {
            // Navigasi kembali ke fragment sebelumnya
            if (NavHostFragment.findNavController(DeliveryAddressFragment.this).popBackStack()) {
                // Berhasil kembali
            } else {
                // Opsional: Tambahkan fallback jika popBackStack() gagal,
                // misalnya jika ini adalah halaman pertama atau ada masalah navigasi.
                // Contoh: requireActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Sangat penting untuk menghapus referensi binding di onDestroyView()
        // untuk menghindari kebocoran memori (memory leaks).
        binding = null;
    }
}
