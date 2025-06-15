package com.example.projectakhir.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.AdoptionRequest;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.example.projectakhir.data.repository.UserReport;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final AdoptionRepository adoptionRepository = new AdoptionRepository();

    private final MutableLiveData<List<AdoptionRequest>> _adoptionRequests = new MutableLiveData<>();
    public LiveData<List<AdoptionRequest>> adoptionRequests = _adoptionRequests;

    private final MutableLiveData<List<UserReport>> _userReports = new MutableLiveData<>();
    public LiveData<List<UserReport>> userReports = _userReports;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<String> _updateStatus = new MutableLiveData<>();
    public LiveData<String> updateStatus = _updateStatus;

    public AdminViewModel() {
        loadAdoptionRequests();
        loadUserReports();
    }

    public void loadAdoptionRequests() {
        _isLoading.setValue(true);
        db.collection("adoption_requests")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AdoptionRequest> requests = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AdoptionRequest request = doc.toObject(AdoptionRequest.class);
                        request.setId(doc.getId()); // <-- PERBAIKI BARIS INI
                        requests.add(request);
                    }
                    _adoptionRequests.setValue(requests);
                    _isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    _error.setValue("Failed to load adoption requests: " + e.getMessage());
                    _isLoading.setValue(false);
                });
    }

    public void loadUserReports() {
        _isLoading.setValue(true);
        db.collection("user_reports")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserReport> reports = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserReport report = doc.toObject(UserReport.class);
                        report.setId(doc.getId()); // <-- PERBAIKI BARIS INI
                        reports.add(report);
                    }
                    _userReports.setValue(reports);
                    _isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    _error.setValue("Failed to load user reports: " + e.getMessage());
                    _isLoading.setValue(false);
                });
    }

    public void updateAdoptionRequestStatus(String docId, String petId, String newStatus) {
        if (docId == null || newStatus == null) {
            _error.setValue("Invalid ID or status.");
            return;
        }

        // Tentukan status hewan baru berdasarkan keputusan admin
        final String newPetStatus;
        if ("Diterima".equals(newStatus)) {
            newPetStatus = "adopted";
        } else if ("Ditolak".equals(newStatus)) {
            newPetStatus = "available"; // Kembalikan menjadi available jika ditolak
        } else {
            // Jika status lain, jangan ubah status hewan
            newPetStatus = null;
        }

        db.collection("adoption_requests").document(docId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // 2. Jika status request berhasil diubah DAN newPetStatus valid, ubah status hewannya
                    if (newPetStatus != null) {
                        adoptionRepository.updatePetAdoptionStatus(petId, newPetStatus, new AdoptionRepository.FirestoreCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                _updateStatus.setValue("Adoption status and pet status updated successfully!");
                                loadAdoptionRequests(); // Muat ulang daftar setelah semua berhasil
                            }
                            @Override
                            public void onError(Exception e) {
                                _error.setValue("Failed to update pet status: " + e.getMessage());
                            }
                        });
                    } else {
                        _updateStatus.setValue("Adoption status updated successfully!");
                        loadAdoptionRequests(); // Muat ulang daftar
                    }
                })
                .addOnFailureListener(e -> _error.setValue("Failed to update adoption status: " + e.getMessage()));
    }

    public void updateUserReportStatus(String docId, String newStatus) {
        if (docId == null || newStatus == null) {
            _error.setValue("Invalid ID or status.");
            return;
        }
        db.collection("user_reports").document(docId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    _updateStatus.setValue("Complaint status updated successfully!");
                    loadUserReports(); // Refresh list
                })
                .addOnFailureListener(e -> _error.setValue("Failed to update complaint status: " + e.getMessage()));
    }

    public void clearMessages() {
        _error.setValue(null);
        _updateStatus.setValue(null);
    }
}