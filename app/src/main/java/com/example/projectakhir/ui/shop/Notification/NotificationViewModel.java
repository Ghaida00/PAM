package com.example.projectakhir.ui.shop.Notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.repository.NotificationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private NotificationRepository notificationRepository;
    private LiveData<List<Notification>> _notifications;

    public NotificationViewModel() {
        notificationRepository = new NotificationRepository();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            _notifications = notificationRepository.getNotifications(currentUser.getUid());
        } else {
            _notifications = new MutableLiveData<>();
        }
    }

    public LiveData<List<Notification>> getNotifications() {
        return _notifications;
    }

    public void markNotificationAsRead(String notificationId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            notificationRepository.markNotificationAsRead(userId, notificationId);
        }
    }

    public void addNotification(Notification notification) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            notification.setUserId(currentUser.getUid());
            notificationRepository.addNotification(notification);
        }
    }

    public void deleteNotification(String notificationId) {
        notificationRepository.deleteNotification(notificationId);
    }
}