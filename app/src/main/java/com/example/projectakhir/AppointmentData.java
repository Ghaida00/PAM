// Buat file baru AppointmentData.java atau taruh di dalam HeartActivity.java
package com.example.projectakhir;

import java.io.Serializable; // Import Serializable jika ingin mengirim objek utuh

// Implement Serializable jika ingin mengirim seluruh objek via Intent
// Jika tidak, kirim field satu per satu
public class AppointmentData /* implements Serializable */ {
    String petName;
    String appointmentDay; // "Today", "Tomorrow", etc.
    String appointmentTime; // "15:00", "14:00", etc.
    String providerName; // Nama Salon/Vet (e.g., "Barber Pet")
    String providerAddress; // Alamat lengkap
    String serviceType; // "Grooming" / "Doctor"
    String serviceDetails; // Detail spesifik (e.g., "Spa - 1 Hour Course")
    String petType; // "Kucing", "Anjing"
    String petNotes; // Catatan hewan
    int iconRes; // Resource ID untuk icon layanan

    // Constructor
    public AppointmentData(String petName, String appointmentDay, String appointmentTime,
                           String providerName, String providerAddress, String serviceType,
                           String serviceDetails, String petType, String petNotes, int iconRes) {
        this.petName = petName;
        this.appointmentDay = appointmentDay;
        this.appointmentTime = appointmentTime;
        this.providerName = providerName;
        this.providerAddress = providerAddress;
        this.serviceType = serviceType;
        this.serviceDetails = serviceDetails;
        this.petType = petType;
        this.petNotes = petNotes;
        this.iconRes = iconRes;
    }

    // Tambahkan getter jika perlu
}