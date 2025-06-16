// File BARU: main/java/com/example/projectakhir/ui/booking/BookingViewModel.java
package com.example.projectakhir.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.repository.AppointmentRepository;
import java.util.List;

public class BookingViewModel extends ViewModel {

    private final AppointmentRepository repository;

    private final MutableLiveData<Boolean> _isSubmitting = new MutableLiveData<>(false);
    public LiveData<Boolean> isSubmitting = _isSubmitting;

    private final MutableLiveData<String> _submissionResult = new MutableLiveData<>();
    public LiveData<String> submissionResult = _submissionResult;

    public BookingViewModel() {
        this.repository = new AppointmentRepository();
    }

    public void createAppointment(String serviceId, String serviceType, String providerName, String petName, String petType, // Tambahkan serviceType
                                  List<String> layananDipilih, String tanggal, String waktu) {
        _isSubmitting.setValue(true);
        _submissionResult.setValue(null);

        repository.createAppointment(serviceId, serviceType, providerName, petName, petType, layananDipilih, tanggal, waktu,
                new AppointmentRepository.FirestoreCallback<String>() {
                    @Override
                    public void onSuccess(String documentId) {
                        _isSubmitting.setValue(false);
                        _submissionResult.setValue("success");
                    }

                    @Override
                    public void onError(Exception e) {
                        _isSubmitting.setValue(false);
                        _submissionResult.setValue("error: " + e.getMessage());
                    }
                });
    }

    public void clearSubmissionResult() {
        _submissionResult.setValue(null);
    }
}