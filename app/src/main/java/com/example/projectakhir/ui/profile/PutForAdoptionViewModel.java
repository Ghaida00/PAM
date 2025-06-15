package com.example.projectakhir.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.example.projectakhir.data.repository.ProfileRepository;

import java.util.List;

public class PutForAdoptionViewModel extends ViewModel {
    private final ProfileRepository profileRepository;
    private final AdoptionRepository adoptionRepository;

    private final MutableLiveData<List<Hewan>> _myPets = new MutableLiveData<>();
    public LiveData<List<Hewan>> myPets = _myPets;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<String> _submissionResult = new MutableLiveData<>();
    public LiveData<String> submissionResult = _submissionResult;

    public PutForAdoptionViewModel() {
        this.profileRepository = new ProfileRepository();
        this.adoptionRepository = new AdoptionRepository();
        fetchMyPets();
    }

    private void fetchMyPets() {
        _isLoading.setValue(true);
        profileRepository.getPetsByOwner(new ProfileRepository.FirestoreCallback<List<Hewan>>() {
            @Override
            public void onSuccess(List<Hewan> result) {
                _myPets.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue(e.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    public void listPetForAdoption(String petId) {
        _isLoading.setValue(true);
        adoptionRepository.updatePetAdoptionStatus(petId, "available", new AdoptionRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _submissionResult.setValue("success");
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _submissionResult.setValue("error: " + e.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    public void clearSubmissionResult() {
        _submissionResult.setValue(null);
    }
}