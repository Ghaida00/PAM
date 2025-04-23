// Buat file baru AppointmentData.java atau taruh di dalam HeartActivity.java
package com.example.projectakhir.data;

// Implement Serializable jika ingin mengirim seluruh objek via Intent
// Jika tidak, kirim field satu per satu
public class AppointmentData /* implements Serializable */ {
    private String petName;
    private String appointmentDay; // "Today", "Tomorrow", etc.
    private String appointmentTime; // "15:00", "14:00", etc.
    private String providerName; // Nama Salon/Vet (e.g., "Barber Pet")
    private String providerAddress; // Alamat lengkap
    private String serviceType; // "Grooming" / "Doctor"
    private String serviceDetails; // Detail spesifik (e.g., "Spa - 1 Hour Course")
    private String petType; // "Kucing", "Anjing"
    private String petNotes; // Catatan hewan
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

    public String getPetName() {
        return petName;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public String getPetType() {
        return petType;
    }

    public String getPetNotes() {
        return petNotes;
    }

    public int getIconRes() {
        return iconRes;
    }

    // Tambahkan getter jika perlu
}