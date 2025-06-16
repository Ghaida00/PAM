package com.example.projectakhir.data.repository;

import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.Notification;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class NotificationRepository {
    private FirestoreSource firestoreSource;

    public NotificationRepository() {
        firestoreSource = new FirestoreSource();
    }

    public LiveData<List<Notification>> getNotifications(String userId) {
        return firestoreSource.getNotifications(userId);
    }

    public Task<Void> markNotificationAsRead(String userId, String notificationId) {
        return firestoreSource.markNotificationAsRead(userId, notificationId);
    }
}