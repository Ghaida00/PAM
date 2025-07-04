// File: main/java/com/example/projectakhir/ui/grooming/DoctorViewModel.java
package com.example.projectakhir.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.Salon;
import com.example.projectakhir.data.repository.GroomingDoctorRepository;
import java.util.ArrayList;
import java.util.List;

public class DoctorViewModel extends ViewModel {

    private final GroomingDoctorRepository repository;
    private final MutableLiveData<List<Salon>> _vetList = new MutableLiveData<>(); // Ganti nama agar lebih jelas
    public LiveData<List<Salon>> vetList = _vetList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public DoctorViewModel() {
        repository = new GroomingDoctorRepository();
        loadVets("Semua"); // Muat semua data dokter saat pertama kali
    }

    public void loadVets(String kategori) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.getFilteredServices("doctor", kategori, new GroomingDoctorRepository.FirestoreCallback<List<Salon>>() {
            @Override
            public void onSuccess(List<Salon> result) {
                _vetList.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat data: " + e.getMessage());
                _vetList.setValue(new ArrayList<>());
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}