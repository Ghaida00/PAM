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
import com.google.firebase.auth.FirebaseAuth;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.example.projectakhir.data.firebase.FirestoreSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import com.example.projectakhir.ui.auth.AuthManager;
import com.example.projectakhir.ui.shop.Checkout.CheckoutViewModelFactory;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private CheckoutViewModel checkoutViewModel;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirestoreSource firestore = new FirestoreSource();
        AuthManager auth = new AuthManager();
        CheckoutViewModelFactory factory = new CheckoutViewModelFactory(firestore, auth);
        checkoutViewModel = new ViewModelProvider(this, factory).get(CheckoutViewModel.class);

        // Set up back button
        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        // Success UI elements (added programmatically)
        View successView = getLayoutInflater().inflate(R.layout.success_checkout_layout, binding.successContainer, false);
        binding.successContainer.addView(successView);
        binding.successContainer.setVisibility(View.GONE);

        binding.btnPay.setOnClickListener(v -> {
            int selectedId = binding.rgPaymentMethods.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Pilih metode pembayaran", Toast.LENGTH_SHORT).show();
            } else {
                final String paymentMethod;
                if (selectedId == R.id.rb_qris) {
                    paymentMethod = "QRIS";
                } else if (selectedId == R.id.rb_bank_transfer) {
                    paymentMethod = "Transfer Bank";
                } else {
                    paymentMethod = "";
                }

                // Ambil userId
                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                if (userId == null) {
                    Toast.makeText(getContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ambil data keranjang sekali dari Firestore
                new FirestoreSource().getCartItemsOnce(userId, cartItems -> {
                    if (cartItems == null || cartItems.isEmpty()) {
                        Toast.makeText(getContext(), "Keranjang kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<String> productIds = new ArrayList<>();
                    Map<String, Integer> productQuantities = new HashMap<>();
                    double totalAmount = 0;
                    for (KeranjangItem item : cartItems) {
                        productIds.add(item.getProductId());
                        productQuantities.put(item.getProductId(), item.getQuantity());
                        totalAmount += item.getProductPrice() * item.getQuantity();
                    }
                    checkoutViewModel.placeOrder(
                        userId,
                        productIds,
                        totalAmount,
                        paymentMethod,
                        productQuantities,
                        documentReference -> {
                            binding.successContainer.setVisibility(View.VISIBLE);
                            binding.tvTitle.setVisibility(View.GONE);
                            binding.tvDeliveryAddress.setVisibility(View.GONE);
                            binding.metodePembayaran.setVisibility(View.GONE);
                            binding.rgPaymentMethods.setVisibility(View.GONE);
                            binding.totalPaymentAmount.setVisibility(View.GONE);
                            binding.btnPay.setVisibility(View.GONE);
                            binding.backButton.setVisibility(View.GONE);
                            successView.setVisibility(View.VISIBLE);

                            ((TextView) successView.findViewById(R.id.paymentMethodText))
                                    .setText("Metode Pembayaran: " + paymentMethod);

                            View finalSuccessView = successView;
                            successView.findViewById(R.id.btnBackToHome).setOnClickListener(btn -> {
                                NavController navController = Navigation.findNavController(finalSuccessView);
                                navController.popBackStack(R.id.blankHomepageFragment, false);
                                navController.navigate(R.id.blankHomepageFragment);
                            });
                        }
                    );
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        rootView = null;
    }

    private void onCheckout(List<KeranjangItem> cartItems, double total) {
        android.util.Log.d("CheckoutDebug", "User checkout dengan total: " + total);
        // ... lanjutkan proses pembayaran
    }
}
