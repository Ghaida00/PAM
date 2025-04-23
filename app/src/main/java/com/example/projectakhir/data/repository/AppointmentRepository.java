// In com.example.projectakhir.data.repository.AppointmentRepository.java
package com.example.projectakhir.data.repository;

import com.example.projectakhir.R;
import com.example.projectakhir.data.AppointmentData; // Use AppointmentData model

import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private ArrayList<AppointmentData> groomingAppointments = new ArrayList<>();
    private ArrayList<AppointmentData> doctorAppointments = new ArrayList<>();

    public AppointmentRepository() {
        setupDummyData();
    }

    private void setupDummyData() {
        // --- MOVE DUMMY DATA CREATION FROM HeartActivity HERE ---
        groomingAppointments.add(new AppointmentData("Grace", "Today", "15:00",
                "Barber Pet", "Jl. Mulyorejo No. 10, Surabaya", "Grooming", "Spa - 1 Hour Course",
                "Kucing", "Note: Nakal Jahil dan Gasuka dipegang perutnya", R.drawable.ic_spa));
        // ... add all other grooming appointments ...

        doctorAppointments.add(new AppointmentData("Grace", "Today", "15:00",
                "Paw Vet", "Jl. Raya Darmo Permai III, Surabaya", "Doctor", "Check-up Rutin",
                "Kucing", "Agak takut orang baru", R.drawable.ic_stethoscope));
        // ... add all other doctor appointments ...
    }

    public List<AppointmentData> getGroomingAppointments() {
        // Return a copy to prevent external modification
        return new ArrayList<>(groomingAppointments);
    }

    public List<AppointmentData> getDoctorAppointments() {
        // Return a copy
        return new ArrayList<>(doctorAppointments);
    }
}