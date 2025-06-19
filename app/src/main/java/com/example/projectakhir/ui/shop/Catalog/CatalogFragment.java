package com.example.projectakhir.ui.shop.Catalog; // PERUBAHAN: dari ui.fragments ke ui.shop

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
import com.example.projectakhir.ui.shop.Catalog.CatalogViewModel; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop
import com.google.firebase.auth.FirebaseAuth;

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

        // Setup click listeners for notification and cart icons
        binding.iconNotification.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_catalogFragment_to_notificationFragment);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error navigating to notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.iconCart.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_catalogFragment_to_keranjangFragment);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error navigating to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        catalogViewModel.products.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateProducts(products);
            }
        });

        catalogViewModel.addToCartStatus.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Produk berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Gagal menambahkan produk ke keranjang. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", product.getId());
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_catalogFragment_to_productDetailFragment, bundle);
    }

    @Override
    public void onAddToCartClick(Product product) {
        if (product == null) {
            Toast.makeText(getContext(), "Produk tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            catalogViewModel.addToCart(product);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Terjadi kesalahan saat menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}