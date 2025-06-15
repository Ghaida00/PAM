package com.example.projectakhir.data.repository;

import android.net.Uri;
import android.util.Log;

import com.example.projectakhir.data.Hewan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptionRepository {

    private static final String TAG = "AdoptionRepository";
    private FirebaseFirestore db;
    private CollectionReference petsCollection;
    private FirebaseStorage storage;

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public interface StorageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(Exception e);
    }


    public AdoptionRepository() {
        db = FirebaseFirestore.getInstance();
        petsCollection = db.collection("pets");
        storage = FirebaseStorage.getInstance();
    }

    public void fetchFilteredHewan(String kota, String kategori, FirestoreCallback<List<Hewan>> callback) {
        Query query = petsCollection.whereEqualTo("adoptionStatus", "available");

        if (kota != null && !kota.isEmpty() && !"Semua Kota".equalsIgnoreCase(kota)) {
            query = query.whereEqualTo("kota", kota);
        }

        if (kategori != null && !kategori.isEmpty() && !"Semua".equalsIgnoreCase(kategori)) {
            query = query.whereEqualTo("jenis", kategori);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Hewan> hewans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Hewan hewan = document.toObject(Hewan.class);
                        hewan.setId(document.getId());
                        hewans.add(hewan);
                    }
                    callback.onSuccess(hewans); // Panggil callback dengan hasil
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching filtered hewan: ", e);
                    callback.onError(e); // Panggil callback dengan error
                });
    }

    public void fetchHewanDetails(String hewanIdOrName, FirestoreCallback<Hewan> callback) {
        petsCollection.document(hewanIdOrName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Hewan hewan = documentSnapshot.toObject(Hewan.class);
                        if (hewan != null) {
                            hewan.setId(documentSnapshot.getId());
                            callback.onSuccess(hewan);
                        } else {
                            // Ini seharusnya tidak terjadi jika exists() true dan konversi berhasil
                            callback.onError(new Exception("Gagal mengkonversi data hewan."));
                        }
                    } else {
                        // Jika tidak ada dengan ID tersebut, coba cari berdasarkan nama
                        fetchHewanByName(hewanIdOrName, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching hewan by ID, trying by name: ", e);
                    fetchHewanByName(hewanIdOrName, callback); // Fallback ke nama
                });
    }

    private void fetchHewanByName(String namaHewan, FirestoreCallback<Hewan> callback) {
        petsCollection.whereEqualTo("nama", namaHewan).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Hewan hewan = document.toObject(Hewan.class);
                        if (hewan != null) {
                            hewan.setId(document.getId());
                            callback.onSuccess(hewan);
                        } else {
                            callback.onError(new Exception("Gagal mengkonversi data hewan (by name)."));
                        }
                    } else {
                        callback.onSuccess(null); // Hewan tidak ditemukan, result null
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching hewan by name: ", e);
                    callback.onError(e);
                });
    }

    public void fetchNewestHewan(int limit, FirestoreCallback<List<Hewan>> callback) {
        petsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("adoptionStatus", "available")
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Hewan> hewans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Hewan hewan = document.toObject(Hewan.class);
                        hewan.setId(document.getId());
                        hewans.add(hewan);
                    }
                    callback.onSuccess(hewans);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching newest hewan: ", e);
                    callback.onError(e);
                });
    }


    private void uploadImageAndGetUrl(Uri imageUri, String folderName, StorageUploadCallback callback) {
        if (imageUri == null) {
            callback.onSuccess(null); // Tidak ada gambar untuk diupload
            return;
        }
        StorageReference photoRef = storage.getReference().child(folderName + "/" + UUID.randomUUID().toString());
        photoRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL: ", e);
                            callback.onError(e);
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed: ", e);
                    callback.onError(e);
                });
    }

    public void addHewanWithImage(Hewan hewan, Uri imageUri, FirestoreCallback<String> callback) {
        uploadImageAndGetUrl(imageUri, "pet_images", new StorageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                if (imageUrl != null) {
                    hewan.setThumbnailImageUrl(imageUrl);
                    hewan.setDetailImageUrl(imageUrl); // Asumsi gambar sama
                }
                // Tambahkan timestamp jika hewan baru, bisa dilakukan di objek Hewan atau di sini
                // Map<String, Object> hewanDataMap = new HashMap<>();
                // ... (isi map dari objek hewan)
                // hewanDataMap.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                petsCollection.add(hewan) // Firestore akan otomatis generate ID
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Hewan added with ID: " + documentReference.getId());
                            callback.onSuccess(documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error adding hewan: ", e);
                            callback.onError(e);
                        });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Image upload failed, cannot add hewan: ", e);
                callback.onError(e);
            }
        });
    }


    public void submitAdoptionForm(String petId, String namaHewan, String jenisHewan, String kotaPengambilan,
                                   String namaPemohon, String alamat, String noHp, String alasan,
                                   String userId, FirestoreCallback<String> callback) {
        Map<String, Object> adoptionData = new HashMap<>();
        adoptionData.put("petId", petId);
        adoptionData.put("namaHewan", namaHewan);
        adoptionData.put("jenisHewan", jenisHewan);         // Simpan Jenis Hewan
        adoptionData.put("kotaPengambilan", kotaPengambilan); // Simpan Kota

        adoptionData.put("namaPemohon", namaPemohon);
        adoptionData.put("alamat", alamat);
        adoptionData.put("noHp", noHp);
        adoptionData.put("alasanAdopsi", alasan);
        adoptionData.put("userId", userId);
        adoptionData.put("status", "Diajukan");
        adoptionData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("adoption_requests")
                .add(adoptionData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Adoption form submitted with ID: " + documentReference.getId());
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error submitting adoption form: ", e);
                    callback.onError(e);
                });
    }

    public void submitPengaduanForm(String namaPelapor, String jenisHewan, String alamatLokasi, String noHpPelapor, String deskripsi, Uri imageUri, String userId, FirestoreCallback<String> callback) {
        uploadImageAndGetUrl(imageUri, "pengaduan_images", new StorageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Map<String, Object> pengaduanData = new HashMap<>();
                pengaduanData.put("namaPelapor", namaPelapor);
                pengaduanData.put("jenisHewan", jenisHewan);
                pengaduanData.put("alamatLokasi", alamatLokasi);
                pengaduanData.put("noHpPelapor", noHpPelapor);
                pengaduanData.put("deskripsiKeadaan", deskripsi);
                if (imageUrl != null) {
                    pengaduanData.put("imageUrl", imageUrl);
                }
                pengaduanData.put("userId", userId);
                pengaduanData.put("status", "Diajukan");
                pengaduanData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                db.collection("user_reports")
                        .add(pengaduanData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Pengaduan submitted with ID: " + documentReference.getId());
                            callback.onSuccess(documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error submitting pengaduan data: ", e);
                            callback.onError(e);
                        });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Image upload failed for pengaduan, cannot submit: ", e);
                callback.onError(e); // Propagate the error
            }
        });
    }

    public void fetchLatestUserAdoptionRequest(String userId, FirestoreCallback<DocumentSnapshot> callback) {
        db.collection("adoption_requests")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    callback.onSuccess(queryDocumentSnapshots.getDocuments().get(0));
                } else {
                    callback.onSuccess(null);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching latest adoption request: ", e);
                callback.onError(e);
            });
    }

    public void fetchLatestUserReport(String userId, FirestoreCallback<DocumentSnapshot> callback) {
        db.collection("user_reports")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    callback.onSuccess(queryDocumentSnapshots.getDocuments().get(0));
                } else {
                    callback.onSuccess(null);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching latest user report: ", e);
                callback.onError(e);
            });
    }

    public void updateAdoptionRequestStatus(String documentId, String newStatus, FirestoreCallback<Void> callback) {
        db.collection("adoption_requests").document(documentId)
            .update("status", newStatus, "timestamp_update", FieldValue.serverTimestamp()) // Tambahkan timestamp update jika perlu
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating adoption request status: ", e);
                callback.onError(e);
            });
    }

    public void updateUserReportStatus(String documentId, String newStatus, FirestoreCallback<Void> callback) {
        db.collection("user_reports").document(documentId)
            .update("status", newStatus, "timestamp_update", FieldValue.serverTimestamp()) // Tambahkan timestamp update jika perlu
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating user report status: ", e);
                callback.onError(e);
            });
    }

    // Method untuk mengubah status hewan di koleksi 'pets'
    public void updatePetAdoptionStatus(String petId, String newStatus, FirestoreCallback<Void> callback) {
        if (petId == null || petId.isEmpty()) {
            callback.onError(new Exception("Pet ID is invalid."));
            return;
        }
        db.collection("pets").document(petId)
                .update(
                        "adoptionStatus", newStatus,
                        "timestamp", FieldValue.serverTimestamp() // Tambahkan baris ini untuk update timestamp
                )
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating pet adoption status: ", e);
                    callback.onError(e);
                });
    }
}