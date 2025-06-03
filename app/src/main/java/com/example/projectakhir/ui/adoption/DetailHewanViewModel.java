package com.example.projectakhir.ui.adoption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;

public class DetailHewanViewModel extends ViewModel {

    private final AdoptionRepository repository;

    private final MutableLiveData<Hewan> _hewanDetail = new MutableLiveData<>();
    public LiveData<Hewan> hewanDetail = _hewanDetail;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public DetailHewanViewModel() {
        // Inisialisasi repository (DI lebih baik untuk produksi)
        this.repository = new AdoptionRepository();
    }

    public void fetchHewanDetailsById(String hewanIdOrName) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.fetchHewanDetails(hewanIdOrName, new AdoptionRepository.FirestoreCallback<Hewan>() {
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
                _hewanDetail.setValue(null); // Atau biarkan data lama jika ada
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}