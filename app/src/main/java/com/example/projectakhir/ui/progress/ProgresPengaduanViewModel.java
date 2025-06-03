package com.example.projectakhir.ui.progress;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProgresPengaduanViewModel extends ViewModel {

    private final AdoptionRepository repository;
    private final MutableLiveData<DocumentSnapshot> _userReport = new MutableLiveData<>();
    public LiveData<DocumentSnapshot> userReport = _userReport;

    private final MutableLiveData<String> _status = new MutableLiveData<>();
    public LiveData<String> status = _status;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private String currentDocumentId = null;

    public ProgresPengaduanViewModel() {
        this.repository = new AdoptionRepository();
        fetchLatestReportProgress();
    }

    public void fetchLatestReportProgress() {
        _isLoading.setValue(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _error.setValue("Pengguna belum login.");
            _isLoading.setValue(false);
            return;
        }
        String userId = currentUser.getUid();

        repository.fetchLatestUserReport(userId, new AdoptionRepository.FirestoreCallback<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot result) {
                _isLoading.setValue(false);
                if (result != null && result.exists()) {
                    _userReport.setValue(result);
                    currentDocumentId = result.getId();
                    _status.setValue(result.getString("status"));
                } else {
                    _error.setValue("Belum ada pengaduan.");
                    _userReport.setValue(null);
                }
            }

            @Override
            public void onError(Exception e) {
                _isLoading.setValue(false);
                _error.setValue("Gagal memuat progres pengaduan: " + e.getMessage());
                _userReport.setValue(null);
            }
        });
    }

    public void cancelReport() {
        if (currentDocumentId == null || _status.getValue() == null) {
            _error.setValue("Tidak ada pengaduan untuk dibatalkan.");
            return;
        }
        // Sesuaikan status akhir pengaduan jika berbeda
        if ("Selesai".equalsIgnoreCase(_status.getValue()) || "Dibatalkan".equalsIgnoreCase(_status.getValue())) {
            _error.setValue("Pengaduan yang sudah " + _status.getValue().toLowerCase() + " tidak dapat dibatalkan.");
            return;
        }

        _isLoading.setValue(true);
        repository.updateUserReportStatus(currentDocumentId, "Dibatalkan", new AdoptionRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.setValue(false);
                _status.setValue("Dibatalkan");
            }

            @Override
            public void onError(Exception e) {
                _isLoading.setValue(false);
                _error.setValue("Gagal membatalkan pengaduan: " + e.getMessage());
            }
        });
    }
    public void clearError() {
        _error.setValue(null);
    }
}