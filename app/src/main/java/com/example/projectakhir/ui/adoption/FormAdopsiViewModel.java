package com.example.projectakhir.ui.adoption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.repository.AdoptionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormAdopsiViewModel extends ViewModel {

    private final AdoptionRepository repository;

    private final MutableLiveData<Boolean> _isSubmitting = new MutableLiveData<>(false);
    public LiveData<Boolean> isSubmitting = _isSubmitting;

    private final MutableLiveData<String> _submissionStatus = new MutableLiveData<>();
    public LiveData<String> submissionStatus = _submissionStatus; // Bisa "success" atau pesan error

    public FormAdopsiViewModel() {
        this.repository = new AdoptionRepository();
    }

    public void submitAdoptionForm(String petId, String petNama, String petJenis, String petKota,
                                   String namaPemohon, String alamat, String noHp, String alasan) {
        _isSubmitting.setValue(true);
        _submissionStatus.setValue(null);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "anonymous"; // Handle jika user tidak login

        repository.submitAdoptionForm(petId, petNama, petJenis, petKota, namaPemohon, alamat, noHp, alasan, userId,
                new AdoptionRepository.FirestoreCallback<String>() {
                    @Override
                    public void onSuccess(String documentId) {
                        _submissionStatus.setValue("success");
                        _isSubmitting.setValue(false);
                    }

                    @Override
                    public void onError(Exception e) {
                        _submissionStatus.setValue("Gagal mengirim pengajuan: " + e.getMessage());
                        _isSubmitting.setValue(false);
                    }
                });
    }

    public void clearSubmissionStatus() {
        _submissionStatus.setValue(null);
    }
}