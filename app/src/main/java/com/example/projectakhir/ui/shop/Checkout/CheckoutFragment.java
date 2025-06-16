package com.example.projectakhir.ui.shop.Checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentCheckoutBinding;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.ui.shop.Checkout.CheckoutViewModel;

import java.util.List;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private CheckoutViewModel checkoutViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        checkoutViewModel.getTotalPrice().observe(getViewLifecycleOwner(), total -> {
            binding.totalPaymentAmount.setText(String.format("Total Pembayaran: Rp %,.0f", total));
        });

        // Set up back button (jika ada di layout CheckoutFragment)
        // Jika Anda memiliki tombol kembali di top bar fragment checkout Anda (mirip keranjang dan notifikasi),
        // maka Anda perlu menambahkan listener di sini.
        // Contoh:
        // binding.backButton.setOnClickListener(v -> {
        //     NavController navController = Navigation.findNavController(v);
        //     navController.popBackStack();
        // });


        binding.btnPay.setOnClickListener(v -> {
            int selectedId = binding.rgPaymentMethods.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Pilih metode pembayaran", Toast.LENGTH_SHORT).show();
            } else {
                String paymentMethod = "";
                if (selectedId == R.id.rb_qris) {
                    paymentMethod = "QRIS";
                } else if (selectedId == R.id.rb_bank_transfer) {
                    paymentMethod = "Transfer Bank";
                }

                // Navigasi ke PaymentActivity (karena PaymentActivity tetap Activity)
                // Atau ke PaymentFragment jika Payment juga diubah menjadi Fragment
                NavController navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putString("payment_method", paymentMethod);
                // Anda juga bisa meneruskan total amount jika diperlukan
                bundle.putDouble("total_amount", checkoutViewModel.getTotalPrice().getValue() != null ? checkoutViewModel.getTotalPrice().getValue() : 0.0);
                // Asumsi PaymentActivity akan tetap menjadi Activity untuk saat ini
                // Jika ingin PaymentActivity juga menjadi Fragment, maka action ini akan berubah ke PaymentFragment
                navController.navigate(R.id.action_checkoutFragment_to_paymentFragment, bundle); // Perlu ditambahkan di nav_graph.xml
            }
        });

        // Contoh: Mengisi alamat pengiriman dari ViewModel atau UserViewModel
        // Anda perlu memuat data alamat dari UserViewModel atau Repository di CheckoutViewModel
        // checkoutViewModel.getDeliveryAddress().observe(getViewLifecycleOwner(), address -> {
        //     binding.tvDeliveryAddress.setText("Alamat Pengiriman: " + address);
        // });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
