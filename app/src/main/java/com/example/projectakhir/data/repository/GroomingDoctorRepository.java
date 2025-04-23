// In com.example.projectakhir.data.repository.GroomingDoctorRepository.java
package com.example.projectakhir.data.repository;

import com.example.projectakhir.R;
import com.example.projectakhir.data.Salon; // Use the Salon model

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroomingDoctorRepository {

    private ArrayList<Salon> allGroomingSalons = new ArrayList<>();
    private ArrayList<Salon> allVetClinics = new ArrayList<>();

    public GroomingDoctorRepository() {
        setupDummyGroomingData();
        setupDummyDoctorData();
    }

    private void setupDummyGroomingData() {
        // --- MOVE DUMMY DATA FROM GroomingActivity HERE ---
        allGroomingSalons.add(new Salon("Barber Pet", "Surabaya", "Open • Close at 20:00",
                new String[]{"Wash", "Brush", "Cut", "Spa"}, R.drawable.grace));
        allGroomingSalons.add(new Salon("Pet Zone", "Surabaya", "Open • Close at 18:00",
                new String[]{"Wash", "Spa"}, R.drawable.grace)); // Consider different image if available
        allGroomingSalons.add(new Salon("Clean Tails", "Surabaya", "Open • Close at 21:00",
                new String[]{"Brush", "Cut"}, R.drawable.claire)); // Consider different image if available
        // Add more dummy grooming salons...
    }

    private void setupDummyDoctorData() {
        // --- MOVE DUMMY DATA FROM DoctorActivity HERE ---
        allVetClinics.add(new Salon("Paw Vet", "Surabaya", "Open • Close at 20:00",
                new String[]{"Vaccine", "GCU", "Medicine"}, R.drawable.grace)); // Use appropriate image
        allVetClinics.add(new Salon("Happy Paw Clinic", "Surabaya", "Open • Close at 18:00",
                new String[]{"Check-up", "Vaccine"}, R.drawable.anomali)); // Use appropriate image
        allVetClinics.add(new Salon("VetCare Center", "Surabaya", "Open • Close at 21:00",
                new String[]{"Emergency", "Medicine"}, R.drawable.amat)); // Use appropriate image
        // Add more dummy vet clinics...
    }

    // Get filtered grooming salons
    public List<Salon> getFilteredGroomingSalons(String kategori) {
        if (kategori.equals("Semua")) {
            return new ArrayList<>(allGroomingSalons); // Return a copy
        }
        return allGroomingSalons.stream()
                .filter(s -> s.menyediakanLayanan(kategori))
                .collect(Collectors.toList());
    }

    // Get filtered vet clinics
    public List<Salon> getFilteredVetClinics(String kategori) {
        if (kategori.equals("Semua")) {
            return new ArrayList<>(allVetClinics); // Return a copy
        }
        return allVetClinics.stream()
                .filter(s -> s.menyediakanLayanan(kategori))
                .collect(Collectors.toList());
    }
}