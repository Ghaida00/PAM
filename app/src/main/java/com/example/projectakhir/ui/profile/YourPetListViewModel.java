package com.example.projectakhir.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.ProfileRepository;
import java.util.List;

public class YourPetListViewModel extends ViewModel {
    private final ProfileRepository repository;

    private final MutableLiveData<List<Hewan>> _petList = new MutableLiveData<List<Hewan>>();
    public LiveData<List<Hewan>> petList = _petList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public YourPetListViewModel() {
        this.repository = new ProfileRepository();
        loadPets();
    }

    public void loadPets() {
        _isLoading.setValue(true);
        repository.getPetsByOwner(new ProfileRepository.FirestoreCallback<List<Hewan>>() {

            @Override
            public void onSuccess(List<Hewan> result) {
                _petList.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Failed to load pets: " + e.getMessage());
                _isLoading.setValue(false);
            }
        });
    }
}