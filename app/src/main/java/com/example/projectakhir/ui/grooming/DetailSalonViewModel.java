// File BARU: main/java/com/example/projectakhir/ui/grooming/DetailSalonViewModel.java
package com.example.projectakhir.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.Salon;
import com.example.projectakhir.data.repository.GroomingDoctorRepository;

public class DetailSalonViewModel extends ViewModel {

    private final GroomingDoctorRepository repository;
    private final MutableLiveData<Salon> _salonDetail = new MutableLiveData<>();
    public LiveData<Salon> salonDetail = _salonDetail;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public DetailSalonViewModel() {
        this.repository = new GroomingDoctorRepository();
    }

    public void fetchSalonDetails(String serviceId) {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.getServiceById(serviceId, new GroomingDoctorRepository.FirestoreCallback<Salon>() {
            @Override
            public void onSuccess(Salon result) {
                _isLoading.setValue(false);
                if (result != null) {
                    _salonDetail.setValue(result);
                } else {
                    _error.setValue("Gagal menemukan detail layanan.");
                }
            }

            @Override
            public void onError(Exception e) {
                _isLoading.setValue(false);
                _error.setValue("Error: " + e.getMessage());
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}