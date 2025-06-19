package com.example.projectakhir.data.repository;

import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.example.projectakhir.data.model.Notification;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class NotificationRepository {
    private FirestoreSource firestoreSource;
    private RealtimeDbSource realtimeDbSource;

    public NotificationRepository() {
        firestoreSource = new FirestoreSource();
        realtimeDbSource = new RealtimeDbSource();
    }

    public LiveData<List<Notification>> getNotifications(String userId) {
        return realtimeDbSource.getNotifications(userId);
    }

    public void markNotificationAsRead(String userId, String notificationId) {
        realtimeDbSource.markNotificationAsRead(userId, notificationId);
    }

    public void addNotification(Notification notification) {
        realtimeDbSource.addNotification(notification);
    }

    public void deleteNotification(String notificationId) {
        realtimeDbSource.deleteNotification(notificationId);
    }

    public Task<Void> markNotificationAsReadTask(String userId, String notificationId) {
        return firestoreSource.markNotificationAsRead(userId, notificationId);
    }

    public void sendOrderNotification(String userId, com.example.projectakhir.data.model.Notification notif) {
        com.google.firebase.database.DatabaseReference notifRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("notifications").child("notifications");
        String key = notifRef.push().getKey();
        notif.setId(key);
        notifRef.child(key).setValue(notif);
        android.util.Log.d("NotifDebug", "Notifikasi order dikirim ke user " + userId + ", notifId: " + key);
    }
}