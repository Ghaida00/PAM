package com.example.projectakhir.ui.shop.Cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.projectakhir.R;
import com.example.projectakhir.adapters.CartAdapter;
import com.example.projectakhir.databinding.FragmentCartBinding;
import com.example.projectakhir.data.model.CartItem;
import com.example.projectakhir.ui.shop.Checkout.CheckoutFragment;
import com.example.projectakhir.ui.shop.Cart.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener {

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    // private UserViewModel userViewModel; // Jika diperlukan
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        // userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class); // Jika diperlukan

        // Set up back button
        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack(); // Kembali ke fragment sebelumnya
        });

        // Setup Cart Items RecyclerView
        cartAdapter = new CartAdapter(new ArrayList<>(), this);
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCartItems.setAdapter(cartAdapter);

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null) {
                cartAdapter.updateCartItems(cartItems);
            }
        });

        cartViewModel.totalPrice.observe(getViewLifecycleOwner(), total -> {
            binding.tvOrderTotal.setText(String.format("Rp %,.0f", total));
            binding.tvGrandTotal.setText(String.format("Rp %,.0f", total));
        });

        binding.btnCheckout.setOnClickListener(v -> {
            List<CartItem> currentCartItems = cartViewModel.getCartItems().getValue();
            if (currentCartItems != null && !currentCartItems.isEmpty()) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_cartFragment_to_checkoutFragment);
            } else {
                Toast.makeText(getContext(), "Keranjang Anda kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnUseVoucher.setOnClickListener(v -> {
            String voucherCode = binding.etVoucherCode.getText().toString().trim();
            if (!voucherCode.isEmpty()) {
                Toast.makeText(getContext(), "Menerapkan voucher: " + voucherCode, Toast.LENGTH_SHORT).show();
                // Implementasi logika voucher di ViewModel atau Repository
            } else {
                Toast.makeText(getContext(), "Masukkan kode voucher.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onQuantityChange(CartItem cartItem, int newQuantity) {
        cartViewModel.updateCartItemQuantity(cartItem.getProductId(), newQuantity);
    }

    @Override
    public void onRemoveItem(CartItem cartItem) {
        cartViewModel.removeCartItem(cartItem.getProductId());
        Toast.makeText(getContext(), cartItem.getProductName() + " dihapus dari keranjang.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
