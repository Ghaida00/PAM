package com.example.projectakhir.ui.adoption;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.repository.AdoptionRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormPengaduanViewModel extends ViewModel {

    private final AdoptionRepository repository;

    private final MutableLiveData<Boolean> _isSubmitting = new MutableLiveData<>(false);
    public LiveData<Boolean> isSubmitting = _isSubmitting;

    // Untuk status: "success", "error: [message]", atau null
    private final MutableLiveData<String> _submissionResult = new MutableLiveData<>();
    public LiveData<String> submissionResult = _submissionResult;

    public FormPengaduanViewModel() {
        this.repository = new AdoptionRepository();
    }

    public void submitPengaduan(String namaPelapor, String jenisHewan, String alamatLokasi,
                                String noHp, String deskripsi, Uri selectedImageUri) {
        _isSubmitting.setValue(true);
        _submissionResult.setValue(null);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "anonymous_reporter";

        repository.submitPengaduanForm(namaPelapor, jenisHewan, alamatLokasi, noHp, deskripsi,
                selectedImageUri, userId, new AdoptionRepository.FirestoreCallback<String>() {
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