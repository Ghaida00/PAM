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

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.adapters.NotificationAdapter;
import com.example.projectakhir.databinding.FragmentNotificationBinding;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.ui.shop.Notification.NotificationViewModel;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        String productIdString = notification.getProductId();
        if (productIdString != null && !productIdString.equalsIgnoreCase("null")) {
            if (productIdString.contains(",")) {
                // Multiple products - load products from Firebase
                loadProductsFromFirebase(productIdString);
            } else {
                // Single product
                String singleProductId = productIdString.trim();
                Bundle bundle = new Bundle();
                loadProductsFromFirebase(singleProductId);
                bundle.putString("productId", singleProductId);
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireView());
            }
        } else {
            Toast.makeText(getContext(), "Notifikasi: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        }
        notificationViewModel.markNotificationAsRead(notification.getId());
    }

    private void loadProductsFromFirebase(String productIds) {
        String[] ids = productIds.split(",");
        List<Product> productsToReview = new ArrayList<>();
        final int totalIds = ids.length;
        final int[] processedCount = {0};

        for (String id : ids) {
            String trimmedId = id.trim();
            realtimeDbSource.getProductById(trimmedId).observe(getViewLifecycleOwner(), product -> {
                android.util.Log.d("ReviewDebug", "Memproses produk: " + trimmedId);
                if (product != null && product.getName() != null) {
                    productsToReview.add(product);
                    android.util.Log.d("ReviewDebug", "Produk ditambahkan: " + product.getName());
                } else {
                    android.util.Log.d("ReviewDebug", "Produk NULL atau tanpa nama: " + trimmedId);
                }

                processedCount[0]++;
                if (processedCount[0] == totalIds) {
                    android.util.Log.d("ReviewDebug", "Total processed: " + processedCount[0]);
                    if (!productsToReview.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("productsToReview", new ArrayList<>(productsToReview));
                        if (getViewLifecycleOwner().getLifecycle().getCurrentState().isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                            androidx.navigation.NavController navController =
                                androidx.navigation.Navigation.findNavController(requireView());
                            navController.navigate(R.id.action_notificationFragment_to_reviewFragment, bundle);
                        } else {
                            android.util.Log.d("ReviewDebug", "Fragment sudah tidak aktif saat navigasi.");
                        }
                    } else {
                        Toast.makeText(getContext(), "Tidak ada produk untuk review", Toast.LENGTH_SHORT).show();
                        android.util.Log.d("ReviewDebug", "productsToReview kosong meskipun sudah diproses semua.");
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