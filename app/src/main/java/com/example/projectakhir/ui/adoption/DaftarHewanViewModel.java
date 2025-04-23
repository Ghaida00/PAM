// In com.example.projectakhir.ui.adoption.DaftarHewanViewModel.java
package com.example.projectakhir.ui.adoption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository; // Import Repository

import java.util.List;

public class DaftarHewanViewModel extends ViewModel {

    private final AdoptionRepository repository;
    private String currentKota = "";

    // LiveData observed by the Activity
    private final MutableLiveData<List<Hewan>> _hewanList = new MutableLiveData<>();
    public LiveData<List<Hewan>> hewanList = _hewanList; // Expose immutable LiveData

    public DaftarHewanViewModel() {
        // In a real app, use Dependency Injection here
        repository = new AdoptionRepository();
    }

    public void loadHewanForCity(String kota) {
        currentKota = kota;
        filterHewan("Semua"); // Load all initially for the city
    }

    public void filterHewan(String kategori) {
        if (currentKota == null || currentKota.isEmpty()) {
            return; // Don't filter if city is not set
        }
        // Get filtered data from repository
        List<Hewan> filteredList = repository.getFilteredHewan(currentKota, kategori);
        // Update LiveData - this will trigger the observer in the Activity
        _hewanList.setValue(filteredList);
    }
}