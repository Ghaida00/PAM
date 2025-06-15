// In main/java/com/example/projectakhir/data/FirestoreDataSeeder.java
package com.example.projectakhir.data;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDataSeeder {

    private static final String TAG = "FirestoreDataSeeder";

    public static void addSampleServices(FirebaseFirestore db) {
        // Menggunakan WriteBatch agar semua data ditulis sekaligus (lebih efisien)
        WriteBatch batch = db.batch();

        // --- GANTI URL DI BAWAH INI DENGAN URL DARI FIREBASE STORAGE ANDA ---
        String urlBarberPet = "https://firebasestorage.googleapis.com/v0/b/pawpal0.firebasestorage.app/o/groom_vet_images%2Fpexels-tima-miroshnichenko-6131540.jpg?alt=media&token=2ee51466-609a-4c4f-8285-79601ab51ece";
        String urlHappyTails = "https://firebasestorage.googleapis.com/v0/b/pawpal0.firebasestorage.app/o/groom_vet_images%2Fpexels-n-voitkevich-4641868.jpg?alt=media&token=399d7062-f63f-4bfc-a413-5dda1bc99d94";
        String urlKlinikSehat = "https://firebasestorage.googleapis.com/v0/b/pawpal0.firebasestorage.app/o/groom_vet_images%2Fimage-48637-800.jpg?alt=media&token=659476e0-5984-4984-97d6-c814f3640e27";
        String urlAnomaliVet = "https://firebasestorage.googleapis.com/v0/b/pawpal0.firebasestorage.app/o/groom_vet_images%2F200109-veterinarian-stock.jpg?alt=media&token=5f286f0b-35bf-452f-99f4-55b7dc626c35";
        // --------------------------------------------------------------------

        // --- DATA CONTOH SALON GROOMING ---

        Map<String, Object> salon1 = new HashMap<>();
        salon1.put("nama", "Barber Pet");
        salon1.put("kota", "Surabaya");
        salon1.put("jam", "10:00 - 19:00");
        salon1.put("tipe", "grooming");
        salon1.put("layanan", Arrays.asList("Wash", "Cut", "Spa", "Brush"));
        salon1.put("imageUrl", urlBarberPet);
        batch.set(db.collection("services").document(), salon1);

        Map<String, Object> salon2 = new HashMap<>();
        salon2.put("nama", "Happy Tails Spa");
        salon2.put("kota", "Jakarta");
        salon2.put("jam", "08:00 - 17:00");
        salon2.put("tipe", "grooming");
        salon2.put("layanan", Arrays.asList("Wash", "Brush", "Spa"));
        salon2.put("imageUrl", urlHappyTails);
        batch.set(db.collection("services").document(), salon2);


        // --- DATA CONTOH KLINIK DOKTER HEWAN ---

        Map<String, Object> vet1 = new HashMap<>();
        vet1.put("nama", "Klinik Hewan Sehat");
        vet1.put("kota", "Surabaya");
        vet1.put("jam", "24 Jam");
        vet1.put("tipe", "doctor");
        vet1.put("layanan", Arrays.asList("Emergency", "Check-up", "Medicine", "GCU"));
        vet1.put("imageUrl", urlKlinikSehat);
        batch.set(db.collection("services").document(), vet1);

        Map<String, Object> vet2 = new HashMap<>();
        vet2.put("nama", "Anomali Vet");
        vet2.put("kota", "Jakarta");
        vet2.put("jam", "10:00 - 22:00");
        vet2.put("tipe", "doctor");
        vet2.put("layanan", Arrays.asList("Vaccine", "Medicine", "Check-up"));
        vet2.put("imageUrl", urlAnomaliVet);
        batch.set(db.collection("services").document(), vet2);

        // Commit batch untuk menulis semua data ke Firestore
        batch.commit().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Data contoh salon dan vet berhasil ditambahkan ke Firestore!");
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Gagal menambahkan data contoh: ", e);
        });
    }
}