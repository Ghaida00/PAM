package com.example.projectakhir.ui.adoption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;

import java.util.ArrayList;
import java.util.List;

public class AdoptHomeViewModel extends ViewModel {

    private final AdoptionRepository repository;
    private final MutableLiveData<List<Hewan>> _newestHewanList = new MutableLiveData<>();
    public LiveData<List<Hewan>> newestHewanList = _newestHewanList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public AdoptHomeViewModel() {
        // Inisialisasi repository (DI lebih baik untuk produksi)
        this.repository = new AdoptionRepository();
        fetchNewestHewan(3); // Ambil 3 hewan terbaru sebagai contoh
    }

    public void fetchNewestHewan(int limit) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.fetchNewestHewan(limit, new AdoptionRepository.FirestoreCallback<List<Hewan>>() {
            @Override
            public void onSuccess(List<Hewan> result) {
                _newestHewanList.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat hewan terbaru: " + e.getMessage());
                _newestHewanList.setValue(new ArrayList<>()); // Set list kosong saat error
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}