package com.example.projectakhir.ui.profile;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.Hewan;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class AddPetViewModel extends ViewModel {
    private final AdoptionRepository repository;

    private final MutableLiveData<Boolean> _isSubmitting = new MutableLiveData<>(false);
    public LiveData<Boolean> isSubmitting = _isSubmitting;

    private final MutableLiveData<String> _submissionResult = new MutableLiveData<>();
    public LiveData<String> submissionResult = _submissionResult;

    public AddPetViewModel() {
        this.repository = new AdoptionRepository();
    }

    // ▼▼▼ PERBARUI PARAMETER METHOD INI ▼▼▼
    public void addPet(String name, String jenis, String age, String sex, String about, List<String> personality, String weight, String location, Uri imageUri) {
        _isSubmitting.setValue(true);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _submissionResult.setValue("error: User not logged in.");
            _isSubmitting.setValue(false);
            return;
        }
        String ownerId = currentUser.getUid();

        Hewan newPet = new Hewan();
        newPet.setOwnerId(ownerId);
        newPet.setNama(name);
        newPet.setJenis(jenis); // Set data jenis hewan
        newPet.setUmur(age);
        newPet.setGender(sex);
        newPet.setDeskripsi(about);
        newPet.setTraits(personality); // Set list kepribadian
        newPet.setBerat(weight + " kg");
        newPet.setKota(location);
        newPet.setAdoptionStatus("none"); // Default status saat ditambahkan

        repository.addHewanWithImage(newPet, imageUri, new AdoptionRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                _submissionResult.setValue("success");
                _isSubmitting.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _submissionResult.setValue("error: " + e.getMessage());
                _isSubmitting.setValue(false);
            }
        });
    }

    public void clearSubmissionResult() {
        _submissionResult.setValue(null);
    }
}
