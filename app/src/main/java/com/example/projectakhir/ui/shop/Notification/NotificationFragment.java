package com.example.projectakhir.ui.shop.Notification; // PERUBAHAN: dari ui.fragments ke ui.shop

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
import com.example.projectakhir.adapters.NotificationAdapter;
import com.example.projectakhir.databinding.FragmentNotificationBinding;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.ui.shop.Notification.NotificationViewModel; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop

import java.util.ArrayList;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private FragmentNotificationBinding binding;
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter notificationAdapter;

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

        notificationAdapter = new NotificationAdapter(new ArrayList<>(), this);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setAdapter(notificationAdapter);

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null) {
                notificationAdapter.updateNotifications(notifications);
            }
        });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        Toast.makeText(getContext(), "Notifikasi: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        notificationViewModel.markNotificationAsRead(notification.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}