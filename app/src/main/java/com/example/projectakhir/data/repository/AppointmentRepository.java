// File: main/java/com/example/projectakhir/data/repository/AppointmentRepository.java
package com.example.projectakhir.data.repository;

import android.net.Uri;
import android.util.Log;
import com.example.projectakhir.data.AppointmentData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AppointmentRepository {

    private static final String TAG = "AppointmentRepository";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public interface StorageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(Exception e);
    }


    public AppointmentRepository() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void fetchAppointmentsForUser(FirestoreCallback<List<DocumentSnapshot>> callback) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            callback.onError(new Exception("Pengguna belum login."));
            return;
        }
        String userId = firebaseUser.getUid();

        db.collection("appointments")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> callback.onSuccess(queryDocumentSnapshots.getDocuments()))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching appointments", e);
                    callback.onError(e);
                });
    }

    public void getAppointmentById(String appointmentId, FirestoreCallback<DocumentSnapshot> callback) {
        db.collection("appointments").document(appointmentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot);
                    } else {
                        callback.onError(new Exception("Appointment tidak ditemukan."));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching appointment by ID", e);
                    callback.onError(e);
                });
    }

    // FUNGSI YANG ERROR TADI, SEKARANG SUDAH ADA
    public void updateAppointmentStatus(String appointmentId, String newStatus, FirestoreCallback<Void> callback) {
        db.collection("appointments").document(appointmentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void uploadPaymentProofAndUpdateUrl(String appointmentId, Uri imageUri, StorageUploadCallback callback) {
        if (imageUri == null) {
            callback.onError(new Exception("URI gambar tidak boleh kosong."));
            return;
        }

        StorageReference proofRef = storage.getReference().child("payment_proofs/" + appointmentId + "/" + UUID.randomUUID().toString());

        proofRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> proofRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            db.collection("appointments").document(appointmentId)
                                    .update("paymentProofUrl", downloadUrl, "status", "Dikonfirmasi")
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(downloadUrl))
                                    .addOnFailureListener(callback::onError);
                        })
                        .addOnFailureListener(callback::onError))
                .addOnFailureListener(callback::onError);
    }

    public void createAppointment(String serviceId, String providerName, String petName,
                                  List<String> layananDipilih, String tanggal, String waktu,
                                  FirestoreCallback<String> callback) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            callback.onError(new Exception("Pengguna belum login."));
            return;
        }
        String userId = firebaseUser.getUid();

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("userId", userId);
        appointment.put("serviceId", serviceId);
        appointment.put("providerName", providerName);
        appointment.put("petName", petName);
        appointment.put("layananDipilih", layananDipilih);
        appointment.put("tanggalDipilih", tanggal);
        appointment.put("waktuDipilih", waktu);
        appointment.put("status", "Menunggu Konfirmasi"); // Status awal
        appointment.put("timestamp", FieldValue.serverTimestamp());

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Appointment created with ID: " + documentReference.getId());
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating appointment", e);
                    callback.onError(e);
                });
    }
}