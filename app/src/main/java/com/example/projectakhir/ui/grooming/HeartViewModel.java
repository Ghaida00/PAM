// File: main/java/com/example/projectakhir/ui/grooming/HeartViewModel.java
package com.example.projectakhir.ui.grooming;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.R;
import com.example.projectakhir.data.AppointmentData;
import com.example.projectakhir.data.repository.AppointmentRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HeartViewModel extends ViewModel {

    private final AppointmentRepository repository;

    private final MutableLiveData<List<AppointmentData>> _groomingList = new MutableLiveData<>();
    public LiveData<List<AppointmentData>> groomingList = _groomingList;

    private final MutableLiveData<List<AppointmentData>> _doctorList = new MutableLiveData<>();
    public LiveData<List<AppointmentData>> doctorList = _doctorList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public HeartViewModel() {
        repository = new AppointmentRepository();
        loadAppointments();
    }

    public void loadAppointments() {
        _isLoading.setValue(true);
        _error.setValue(null);
        repository.fetchAppointmentsForUser(new AppointmentRepository.FirestoreCallback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> result) {
                List<AppointmentData> allAppointments = new ArrayList<>();
                for (DocumentSnapshot doc : result) {
                    AppointmentData data = mapDocumentToAppointmentData(doc);
                    if (data != null) {
                        allAppointments.add(data);
                    }
                }

                // Filter daftar menjadi grooming dan doctor
                _groomingList.setValue(allAppointments.stream()
                        .filter(app -> "grooming".equalsIgnoreCase(app.getServiceType()))
                        .collect(Collectors.toList()));

                _doctorList.setValue(allAppointments.stream()
                        .filter(app -> "doctor".equalsIgnoreCase(app.getServiceType()))
                        .collect(Collectors.toList()));

                _isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                _isLoading.setValue(false);
                _error.setValue("Gagal memuat janji temu: " + e.getMessage());
            }
        });
    }

    private AppointmentData mapDocumentToAppointmentData(DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return null;

        String serviceType = doc.getString("serviceType");
        if (serviceType == null) { return null; }

        List<String> layananList = (List<String>) doc.get("layananDipilih");
        String serviceDetails = "";
        if (layananList != null) {
            serviceDetails = String.join(", ", layananList);
        }

        int iconRes = R.drawable.ic_self_care;
        if ("grooming".equalsIgnoreCase(serviceType)) {
            iconRes = R.drawable.ic_spa;
        } else if ("doctor".equalsIgnoreCase(serviceType)) {
            iconRes = R.drawable.ic_stethoscope;
        }

        String petType = doc.getString("petType");
        if (petType == null || petType.isEmpty()) {
            petType = "Hewan"; // Fallback
        }

        String appointmentDate = doc.getString("tanggalDipilih");
        String displayDay = getRelativeDayString(appointmentDate);
        AppointmentData data = new AppointmentData(
                doc.getString("petName"),
                displayDay,
                doc.getString("waktuDipilih"),
                doc.getString("providerName"),
                "Alamat belum tersedia", // Alamat bisa ditambahkan saat booking jika perlu
                serviceType,
                serviceDetails,
                petType,
                "Tidak ada catatan", // Catatan bisa ditambahkan saat booking
                iconRes
        );
        data.setId(doc.getId()); // Simpan ID dokumen
        return data;
    }

    private String getRelativeDayString(String appointmentDateStr) {
        if (appointmentDateStr == null || appointmentDateStr.isEmpty()) {
            return "Unknown Date"; // Fallback jika tanggal tidak ada
        }

        // Format yang sama seperti yang Anda gunakan di BookingFragment
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d", Locale.ENGLISH);

        // Dapatkan tanggal hari ini dan besok dalam format yang sama
        Calendar todayCal = Calendar.getInstance();
        String todayStr = sdf.format(todayCal.getTime());

        Calendar tomorrowCal = Calendar.getInstance();
        tomorrowCal.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowStr = sdf.format(tomorrowCal.getTime());

        // Bandingkan
        if (appointmentDateStr.equalsIgnoreCase(todayStr)) {
            return "Today";
        } else if (appointmentDateStr.equalsIgnoreCase(tomorrowStr)) {
            return "Tomorrow";
        } else {
            // Jika bukan hari ini atau besok, tampilkan tanggal aslinya
            return appointmentDateStr;
        }
    }
}