// File: main/java/com/example/projectakhir/data/repository/GroomingDoctorRepository.java
package com.example.projectakhir.data.repository;

import android.util.Log;
import com.example.projectakhir.data.Salon;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class GroomingDoctorRepository {

    private static final String TAG = "GroomingDoctorRepo";
    private final CollectionReference servicesCollection;

    // Callback interface untuk operasi Firestore yang asynchronous
    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public GroomingDoctorRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        servicesCollection = db.collection("services");
    }

    // Mengambil data services (grooming/doctor) berdasarkan tipe dan kategori
    public void getFilteredServices(String tipe, String kategori, FirestoreCallback<List<Salon>> callback) {
        Query query = servicesCollection.whereEqualTo("tipe", tipe);

        if (kategori != null && !kategori.isEmpty() && !"Semua".equalsIgnoreCase(kategori)) {
            query = query.whereArrayContains("layanan", kategori);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Salon> serviceList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Salon salon = document.toObject(Salon.class);
                        salon.setId(document.getId()); // Simpan ID dokumen
                        serviceList.add(salon);
                    }
                    callback.onSuccess(serviceList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching filtered services: ", e);
                    callback.onError(e);
                });
    }

    // Mengambil satu service berdasarkan ID dokumennya
    public void getServiceById(String serviceId, FirestoreCallback<Salon> callback) {
        servicesCollection.document(serviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Salon salon = documentSnapshot.toObject(Salon.class);
                        if (salon != null) {
                            salon.setId(documentSnapshot.getId());
                            callback.onSuccess(salon);
                        } else {
                            callback.onError(new Exception("Gagal parsing data service."));
                        }
                    } else {
                        callback.onSuccess(null); // Dokumen tidak ditemukan
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching service by ID: ", e);
                    callback.onError(e);
                });
    }
}