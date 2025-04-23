// In com.example.projectakhir.ui.grooming.HeartViewModel.java
package com.example.projectakhir.ui.grooming; // Or adjust package

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.AppointmentData;
import com.example.projectakhir.data.repository.AppointmentRepository; // Import Repository

import java.util.List;

public class HeartViewModel extends ViewModel {

    private final AppointmentRepository repository;

    private final MutableLiveData<List<AppointmentData>> _groomingList = new MutableLiveData<>();
    public LiveData<List<AppointmentData>> groomingList = _groomingList;

    private final MutableLiveData<List<AppointmentData>> _doctorList = new MutableLiveData<>();
    public LiveData<List<AppointmentData>> doctorList = _doctorList;

    public HeartViewModel() {
        repository = new AppointmentRepository(); // Use DI in real app
        loadAppointments();
    }

    public void loadAppointments() {
        _groomingList.setValue(repository.getGroomingAppointments());
        _doctorList.setValue(repository.getDoctorAppointments());
    }
}