package com.example.projectakhir.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Untuk menampilkan pesan singkat

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

// Import kelas Binding yang sesuai dengan nama file XML kamu
import com.example.projectakhir.databinding.FragmentPaymentMethodBinding;
// Jika R tidak bisa diakses langsung, import R dari module aplikasi mu, misal:
// import com.example.projectakhir.R;


public class PaymentMethodFragment extends Fragment {

    // Deklarasi variabel binding
    private FragmentPaymentMethodBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout menggunakan ViewBinding
        binding = FragmentPaymentMethodBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup listener untuk aksi-aksi di fragment ini
        setupActionListeners();
    }

    private void setupActionListeners() {
        // Listener untuk tombol kembali di toolbar
        binding.btnBackPayment.setOnClickListener(v -> {
            // Navigasi kembali ke fragment sebelumnya
            if (NavHostFragment.findNavController(PaymentMethodFragment.this).popBackStack()) {
                // Berhasil kembali
            } else {
                // Opsional: Fallback jika popBackStack() gagal
            }
        });

        // Listener untuk item Cash
        binding.itemCash.setOnClickListener(v -> {
            // TODO: Implementasikan aksi saat item Cash diklik
            showToast("Metode Cash dipilih");
            // Contoh navigasi:
            // NavHostFragment.findNavController(this).navigate(R.id.action_paymentMethodFragment_to_cashDetailsFragment);
        });

        // Listener untuk item Master Card
        binding.itemMasterCard.setOnClickListener(v -> {
            // TODO: Implementasikan aksi saat item Master Card diklik
            showToast("Master Card dipilih");
            // Contoh navigasi:
            // NavHostFragment.findNavController(this).navigate(R.id.action_paymentMethodFragment_to_cardDetailsFragment);
        });

        // Listener untuk item Visa Card
        binding.itemVisaCard.setOnClickListener(v -> {
            // TODO: Implementasikan aksi saat item Visa Card diklik
            showToast("Visa Card dipilih");
            // Contoh navigasi:
            // NavHostFragment.findNavController(this).navigate(R.id.action_paymentMethodFragment_to_cardDetailsFragment);
        });

        // Listener untuk item Add New Card
        binding.itemAddNewCard.setOnClickListener(v -> {
            // TODO: Implementasikan aksi untuk menambah kartu baru
            showToast("Tambah Kartu Baru diklik");
            // Contoh navigasi:
            // NavHostFragment.findNavController(this).navigate(R.id.action_paymentMethodFragment_to_addNewCardFragment);
        });

        // Listener untuk item Bank Transfer
        binding.itemBankTransfer.setOnClickListener(v -> {
            // TODO: Implementasikan aksi saat item Bank Transfer diklik
            showToast("Bank Transfer dipilih");
        });

        // Listener untuk item Mobile Transfer
        binding.itemMobileTransfer.setOnClickListener(v -> {
            // TODO: Implementasikan aksi saat item Mobile Transfer diklik
            showToast("Mobile Transfer dipilih");
        });
    }

    // Helper method untuk menampilkan Toast
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Penting untuk menghapus referensi binding di onDestroyView()
        // untuk menghindari kebocoran memori (memory leaks).
        binding = null;
    }
}
