package com.example.projectakhir.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;

public class DetailPetViewModel extends ViewModel {
    private final AdoptionRepository repository;
    private final MutableLiveData<Hewan> _hewanDetail = new MutableLiveData<>();
    public LiveData<Hewan> hewanDetail = _hewanDetail;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public DetailPetViewModel() {
        this.repository = new AdoptionRepository();
    }

    public void fetchHewanDetailsById(String hewanId) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.fetchHewanDetails(hewanId, new AdoptionRepository.FirestoreCallback<Hewan>() {
            @Override
            public void onSuccess(Hewan result) {
                _hewanDetail.setValue(result);
                _isLoading.setValue(false);
                if (result == null) {
                    _error.setValue("Data hewan tidak ditemukan.");
                }
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat detail hewan: " + e.getMessage());
                _hewanDetail.setValue(null);
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}