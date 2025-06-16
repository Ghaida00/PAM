package com.example.projectakhir.ui.shop.Keranjang; // PERUBAHAN: dari ui.fragments ke ui.shop

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.projectakhir.adapters.KeranjangAdapter;
import com.example.projectakhir.databinding.FragmentKeranjangBinding;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.ui.shop.Checkout.CheckoutFragment;
import com.example.projectakhir.ui.shop.Keranjang.KeranjangViewModel; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop

import java.util.ArrayList;
import java.util.List;

public class KeranjangFragment extends Fragment implements KeranjangAdapter.OnCartItemActionListener {

    private FragmentKeranjangBinding binding;
    private KeranjangViewModel cartViewModel;
    private KeranjangAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentKeranjangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(this).get(KeranjangViewModel.class);

        cartAdapter = new KeranjangAdapter(new ArrayList<>(), this);
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCartItems.setAdapter(cartAdapter);

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null) {
                cartAdapter.updateCartItems(cartItems);
            }
        });

        binding.btnCheckout.setOnClickListener(v -> {
            List<KeranjangItem> currentCartItems = cartViewModel.getCartItems().getValue();
            if (currentCartItems != null && !currentCartItems.isEmpty()) {
                startActivity(new Intent(getContext(), CheckoutFragment.class));
            } else {
                Toast.makeText(getContext(), "Keranjang Anda kosong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQuantityChange(KeranjangItem cartItem, int newQuantity) {
        cartViewModel.updateCartItemQuantity(cartItem.getProductId(), newQuantity);
    }

    @Override
    public void onRemoveItem(KeranjangItem cartItem) {
        cartViewModel.removeCartItem(cartItem.getProductId());
        Toast.makeText(getContext(), cartItem.getProductName() + " dihapus dari keranjang.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
