package com.example.projectakhir.ui.shop.Payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentPaymentBinding;
import com.example.projectakhir.ui.shop.Payment.PaymentViewModel;

public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private PaymentViewModel paymentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        // Dapatkan argumen dari NavComponent
        String paymentMethod = null;
        double totalAmount = 0.0;
        if (getArguments() != null) {
            paymentMethod = getArguments().getString("payment_method");
            totalAmount = getArguments().getDouble("total_amount", 0.0);
        }

        // Tampilkan UI sesuai metode pembayaran
        if ("QRIS".equals(paymentMethod)) {
            binding.qrisLayout.setVisibility(View.VISIBLE);
            binding.successLayout.setVisibility(View.GONE);
            // Anda bisa tambahkan logika untuk menampilkan QRIS yang sebenarnya di sini
            // (misalnya, memuat dari URL atau membuat QR code)
        } else if ("Transfer Bank".equals(paymentMethod)) {
            binding.qrisLayout.setVisibility(View.GONE);
            binding.successLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Instruksi Transfer Bank akan dikirimkan.", Toast.LENGTH_LONG).show();
            // Anda bisa tambahkan detail transfer bank di sini
        } else {
            // Default: tampilkan sukses atau pesan error
            binding.qrisLayout.setVisibility(View.GONE);
            binding.successLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Metode pembayaran tidak dikenal, pembayaran berhasil.", Toast.LENGTH_SHORT).show();
        }

        // Listener untuk tombol kembali ke beranda (Payment Successful)
        binding.btnBackToHome.setOnClickListener(v -> navigateToHomePage());

        // Listener untuk tombol kembali ke beranda (QRIS)
        binding.btnBackToHomeQris.setOnClickListener(v -> navigateToHomePage());
    }

    private void navigateToHomePage() {
        // Kembali ke BlankHomepageFragment (halaman utama) dan bersihkan back stack
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack(R.id.blankHomepageFragment, false); // Pop up ke homepage, tidak inklusif
        navController.navigate(R.id.blankHomepageFragment); // Navigasi ke homepage untuk refresh UI
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
