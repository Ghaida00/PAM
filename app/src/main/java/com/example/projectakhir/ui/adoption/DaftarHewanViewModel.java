package com.example.projectakhir.ui.adoption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;

import java.util.ArrayList; // Import ArrayList
import java.util.List;

public class DaftarHewanViewModel extends ViewModel {

    private final AdoptionRepository repository;
    private String currentKota = "";
    private String currentKategori = "Semua";

    private final MutableLiveData<List<Hewan>> _hewanList = new MutableLiveData<>();
    public LiveData<List<Hewan>> hewanList = _hewanList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public DaftarHewanViewModel() {
        // Jika menggunakan Hilt atau DI framework lain, repository akan di-inject.
        // Untuk sekarang, kita masih inisialisasi manual.
        this.repository = new AdoptionRepository();
    }

    // Konstruktor untuk Dependency Injection (jika Anda setup Hilt/Dagger nanti)
    // public DaftarHewanViewModel(AdoptionRepository repository) {
    //    this.repository = repository;
    // }

    public void loadHewanForCity(String kota) {
        currentKota = kota;
        currentKategori = "Semua"; // Reset kategori saat kota berubah
        fetchHewanFromRepository();
    }

    public void filterHewan(String kategori) {
        currentKategori = kategori;
        fetchHewanFromRepository();
    }

    private void fetchHewanFromRepository() {
        if (currentKota == null || currentKota.isEmpty()) {
            _error.setValue("Kota belum dipilih.");
            _hewanList.setValue(new ArrayList<>()); // Set list kosong
            _isLoading.setValue(false); // Hentikan loading
            return;
        }
        _isLoading.setValue(true);
        _error.setValue(null); // Bersihkan error sebelumnya

        repository.fetchFilteredHewan(currentKota, currentKategori, new AdoptionRepository.FirestoreCallback<List<Hewan>>() {
            @Override
            public void onSuccess(List<Hewan> result) {
                _hewanList.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat data hewan: " + e.getMessage());
                _hewanList.setValue(new ArrayList<>()); // Set list kosong saat error
                _isLoading.setValue(false);
            }
        });
    }

    public void clearError() {
        _error.setValue(null);
    }
}