// File BARU: main/java/com/example/projectakhir/ui/booking/AppointmentDetailViewModel.java
package com.example.projectakhir.ui.booking;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.repository.AppointmentRepository;
import com.google.firebase.firestore.DocumentSnapshot;

public class AppointmentDetailViewModel extends ViewModel {

    private final AppointmentRepository repository;
    private String appointmentId;

    private final MutableLiveData<DocumentSnapshot> _appointmentDetail = new MutableLiveData<>();
    public LiveData<DocumentSnapshot> appointmentDetail = _appointmentDetail;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<String> _updateResult = new MutableLiveData<>();
    public LiveData<String> updateResult = _updateResult;


    public AppointmentDetailViewModel() {
        this.repository = new AppointmentRepository();
    }

    public void loadAppointmentDetails(String id) {
        this.appointmentId = id;
        _isLoading.setValue(true);
        _error.setValue(null);

        repository.getAppointmentById(appointmentId, new AppointmentRepository.FirestoreCallback<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot result) {
                _appointmentDetail.setValue(result);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal memuat detail: " + e.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    public void cancelAppointment() {
        if (appointmentId == null) {
            _error.setValue("ID Appointment tidak ditemukan.");
            return;
        }
        _isLoading.setValue(true);
        repository.updateAppointmentStatus(appointmentId, "Dibatalkan", new AppointmentRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _updateResult.setValue("cancelled_success");
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal membatalkan: " + e.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    public void confirmAppointmentWithProof(Uri imageUri) {
        if (appointmentId == null) {
            _error.setValue("ID Appointment tidak ditemukan.");
            return;
        }
        if (imageUri == null) {
            _error.setValue("Bukti pembayaran harus diunggah.");
            return;
        }
        _isLoading.setValue(true);
        repository.uploadPaymentProofAndUpdateUrl(appointmentId, imageUri, new AppointmentRepository.StorageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                _updateResult.setValue("confirmed_success");
                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue("Gagal konfirmasi: " + e.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    public void clearUpdateResult() {
        _updateResult.setValue(null);
    }
    public void clearError() {
        _error.setValue(null);
    }
}