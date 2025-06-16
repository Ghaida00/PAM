package com.example.projectakhir.ui.shop.Catalog;

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
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.projectakhir.R;
import com.example.projectakhir.adapters.ProductAdapter;
import com.example.projectakhir.databinding.FragmentCatalogBinding;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.ui.shop.Catalog.CatalogViewModel;

import java.util.ArrayList;

public class CatalogFragment extends Fragment implements ProductAdapter.OnItemClickListener {

    private FragmentCatalogBinding binding;
    private CatalogViewModel catalogViewModel;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        catalogViewModel = new ViewModelProvider(this).get(CatalogViewModel.class);

        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 kolom
        binding.productsRecyclerView.setAdapter(productAdapter);

        catalogViewModel.products.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateProducts(products);
            }
        });

        catalogViewModel.addToCartStatus.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Produk ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Gagal menambahkan produk ke keranjang. Pastikan Anda login.", Toast.LENGTH_SHORT).show();
            }
        });

        // Tambahkan navigasi icon bell ke notificationFragment
        binding.iconNotification.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.notificationFragment);
        });

        // Tambahkan navigasi icon cart ke cartFragment
        binding.iconCart.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.cartFragment);
        });
    }

    @Override
    public void onProductClick(Product product) {
        NavController navController = Navigation.findNavController(requireView());
        Bundle args = new Bundle();
        args.putString("productId", product.getId());
        navController.navigate(R.id.action_catalogFragment_to_detailFragment, args);
    }

    @Override
    public void onAddToCartClick(Product product) {
        catalogViewModel.addToCart(product);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}