package com.example.projectakhir.ui.shop.Notification;

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

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.NotificationAdapter;
import com.example.projectakhir.databinding.FragmentNotificationBinding;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.ui.shop.Notification.NotificationViewModel;
import com.example.projectakhir.data.firebase.RealtimeDbSource;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private FragmentNotificationBinding binding;
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter notificationAdapter;
    private RealtimeDbSource realtimeDbSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        realtimeDbSource = new RealtimeDbSource();

        notificationAdapter = new NotificationAdapter(new ArrayList<>(), this);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setAdapter(notificationAdapter);

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null) {
                notificationAdapter.updateNotifications(notifications);
            }
        });

        binding.backButton.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
        });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        if ("REVIEW".equals(notification.getActionType()) && notification.getProductId() != null) {
            // Handle multi-product review
            if (notification.getProductId().contains(",")) {
                // Multiple products - load products from Firebase
                loadProductsFromFirebase(notification.getProductId());
            } else {
                // Single product
                Bundle bundle = new Bundle();
                bundle.putString("productId", notification.getProductId());
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireView());
                navController.navigate(R.id.action_notificationFragment_to_reviewFragment, bundle);
            }
        } else {
            Toast.makeText(getContext(), "Notifikasi: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        }
        notificationViewModel.markNotificationAsRead(notification.getId());
    }

    private void loadProductsFromFirebase(String productIds) {
        String[] ids = productIds.split(",");
        List<Product> productsToReview = new ArrayList<>();
        final int[] loadedCount = {0};
        
        for (String id : ids) {
            String trimmedId = id.trim();
            realtimeDbSource.getProductById(trimmedId).observe(getViewLifecycleOwner(), product -> {
                if (product != null) {
                    productsToReview.add(product);
                }
                loadedCount[0]++;
                
                // If all products are loaded, navigate to review
                if (loadedCount[0] == ids.length) {
                    if (!productsToReview.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("productsToReview", new ArrayList<>(productsToReview));
                        androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_notificationFragment_to_reviewFragment, bundle);
                    } else {
                        Toast.makeText(getContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}