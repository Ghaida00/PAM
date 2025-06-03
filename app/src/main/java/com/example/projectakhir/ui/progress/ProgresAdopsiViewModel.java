package com.example.projectakhir.ui.progress;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProgresAdopsiViewModel extends ViewModel {

    private static final String TAG = "ProgresAdopsiVM";
    private final AdoptionRepository repository;
    private final MutableLiveData<DocumentSnapshot> _adoptionRequest = new MutableLiveData<>();
    public LiveData<DocumentSnapshot> adoptionRequest = _adoptionRequest;

    private final MutableLiveData<String> _hewanNama = new MutableLiveData<>();
    public LiveData<String> hewanNama = _hewanNama;

    private final MutableLiveData<String> _hewanJenis = new MutableLiveData<>();
    public LiveData<String> hewanJenis = _hewanJenis;

    private final MutableLiveData<String> _hewanKota = new MutableLiveData<>();
    public LiveData<String> hewanKota = _hewanKota;


    private final MutableLiveData<String> _status = new MutableLiveData<>();
    public LiveData<String> status = _status;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private String currentDocumentId = null;

    public ProgresAdopsiViewModel() {
        this.repository = new AdoptionRepository();
        fetchLatestAdoptionProgress();
    }

    public void fetchLatestAdoptionProgress() {
        _isLoading.setValue(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _error.setValue("Pengguna belum login.");
            _isLoading.setValue(false);
            return;
        }
        String userId = currentUser.getUid();

        repository.fetchLatestUserAdoptionRequest(userId, new AdoptionRepository.FirestoreCallback<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot result) {
                _isLoading.setValue(false);
                if (result != null && result.exists()) {
                    _adoptionRequest.setValue(result);
                    currentDocumentId = result.getId();
                    _status.setValue(result.getString("status"));

                    // Ambil namaHewan dari request
                    String namaHewanFromRequest = result.getString("namaHewan");
                    _hewanNama.setValue(namaHewanFromRequest);


                    // Ambil detail hewan (jenis & kota) berdasarkan namaHewanFromRequest
                    if (namaHewanFromRequest != null && !namaHewanFromRequest.isEmpty()) {
                        repository.fetchHewanDetails(namaHewanFromRequest, new AdoptionRepository.FirestoreCallback<com.example.projectakhir.data.Hewan>() {
                            @Override
                            public void onSuccess(com.example.projectakhir.data.Hewan hewanDetail) {
                                if (hewanDetail != null) {
                                    _hewanJenis.setValue(hewanDetail.getJenis());
                                    _hewanKota.setValue(hewanDetail.getKota());
                                } else {
                                    _hewanJenis.setValue("N/A");
                                    _hewanKota.setValue("N/A");
                                    Log.w(TAG, "Detail hewan tidak ditemukan untuk: " + namaHewanFromRequest);
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                _hewanJenis.setValue("Error");
                                _hewanKota.setValue("Error");
                                Log.e(TAG, "Error fetching hewan detail: ", e);
                            }
                        });
                    } else {
                        _hewanJenis.setValue("N/A");
                        _hewanKota.setValue("N/A");
                    }


                } else {
                    _error.setValue("Belum ada pengajuan adopsi.");
                    _adoptionRequest.setValue(null); // Pastikan UI tahu tidak ada data
                }
            }

            @Override
            public void onError(Exception e) {
                _isLoading.setValue(false);
                _error.setValue("Gagal memuat progres adopsi: " + e.getMessage());
                _adoptionRequest.setValue(null);
            }
        });
    }

    public void cancelAdoption() {
        if (currentDocumentId == null || _status.getValue() == null) {
            _error.setValue("Tidak ada pengajuan untuk dibatalkan.");
            return;
        }
        if ("Diterima".equalsIgnoreCase(_status.getValue()) || "Dibatalkan".equalsIgnoreCase(_status.getValue())) {
            _error.setValue("Pengajuan yang sudah " + _status.getValue().toLowerCase() + " tidak dapat dibatalkan.");
            return;
        }

        _isLoading.setValue(true);
        repository.updateAdoptionRequestStatus(currentDocumentId, "Dibatalkan", new AdoptionRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.setValue(false);
                _status.setValue("Dibatalkan");
                // Opsional: refresh data _adoptionRequest jika perlu FieldValue.serverTimestamp()
                // fetchLatestAdoptionProgress(); 
            }

            @Override
            public void onError(Exception e) {
                _isLoading.setValue(false);
                _error.setValue("Gagal membatalkan pengajuan: " + e.getMessage());
            }
        });
    }
    public void clearError() {
        _error.setValue(null);
    }
}