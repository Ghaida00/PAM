package com.example.projectakhir.mealplannerfragment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectakhir.R;
import com.example.projectakhir.mealplannerfragment.adapters.CartItemAdapter;
import com.example.projectakhir.mealplannerfragment.models.CartItem;
import com.example.projectakhir.mealplannerfragment.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerCart;
    private Button btnCustomize;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private List<CartItem> selectedCartItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerCart = view.findViewById(R.id.recycler_cart);
        btnCustomize = view.findViewById(R.id.btn_customize);

        // Always show all items in the cart
        cartItems = CartManager.getInstance().getCartItems();
        adapter = new CartItemAdapter(requireContext(), cartItems);
        recyclerCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCart.setAdapter(adapter);

        adapter.setOnCartItemChangeListener(new CartItemAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(CartItem cartItem, int position) {
                // Update UI if needed
            }
            @Override
            public void onSelectionChanged(CartItem cartItem, int position, boolean isSelected) {
                if (isSelected) {
                    if (!selectedCartItems.contains(cartItem)) {
                        selectedCartItems.add(cartItem);
                    }
                } else {
                    selectedCartItems.remove(cartItem);
                }
            }
        });

        btnCustomize.setOnClickListener(v -> {
            if (selectedCartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one item", Toast.LENGTH_SHORT).show();
            } else {
                CartManager.getInstance().setSelectedCartItems(selectedCartItems);
                NavHostFragment.findNavController(this).navigate(R.id.action_cartFragment_to_customizedMenuFragment);
            }
        });
    }
}