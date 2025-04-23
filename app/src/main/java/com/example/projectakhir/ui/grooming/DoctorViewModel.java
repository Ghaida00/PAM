// Example: GroomingViewModel.java (DoctorViewModel is analogous)
package com.example.projectakhir.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Salon;
import com.example.projectakhir.data.repository.GroomingDoctorRepository; // Import Repository

import java.util.List;

public class DoctorViewModel extends ViewModel {

    private final GroomingDoctorRepository repository;

    private final MutableLiveData<List<Salon>> _salonList = new MutableLiveData<>();
    public LiveData<List<Salon>> salonList = _salonList;

    public DoctorViewModel() {
        repository = new GroomingDoctorRepository(); // Use DI in real app
        loadSalons("Semua"); // Load all initially
    }

    public void loadSalons(String kategori) {
        List<Salon> filteredList = repository.getFilteredVetClinics(kategori);
        _salonList.setValue(filteredList);
    }
}