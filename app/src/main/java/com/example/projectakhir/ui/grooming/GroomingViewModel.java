// File: main/java/com/example/projectakhir/ui/grooming/GroomingViewModel.java
package com.example.projectakhir.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.Salon;
import com.example.projectakhir.data.repository.GroomingDoctorRepository;
import java.util.ArrayList;
import java.util.List;

public class GroomingViewModel extends ViewModel {

    private final GroomingDoctorRepository repository;
    private final MutableLiveData<List<Salon>> _salonList = new MutableLiveData<>();
    public LiveData<List<Salon>> salonList = _salonList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public GroomingViewModel() {
        repository = new GroomingDoctorRepository();
        loadSalons("Semua"); // Muat semua data grooming saat pertama kali
    }

    public void loadSalons(String kategori) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.getFilteredServices("grooming", kategori, new GroomingDoctorRepository.FirestoreCallback<List<Salon>>() {
            @Override
            public void onSuccess(List<Salon> result) {
                _salonList.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat data: " + e.getMessage());
                _salonList.setValue(new ArrayList<>());
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}