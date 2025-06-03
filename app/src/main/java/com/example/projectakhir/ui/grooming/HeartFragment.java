package com.example.projectakhir.ui.grooming;

import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController; // Import NavController
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.AppointmentAdapter;
import com.example.projectakhir.data.AppointmentData;
import com.example.projectakhir.databinding.FragmentHeartBinding;

import java.util.ArrayList;

public class HeartFragment extends Fragment {

    private FragmentHeartBinding binding;
    private AppointmentAdapter groomingAdapter;
    private AppointmentAdapter doctorAdapter;
    private HeartViewModel viewModel;
    // Tidak perlu NavController instance variabel jika menggunakan NavHostFragment.findNavController(this) secara langsung

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHeartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HeartViewModel.class);

        // --- Setup Navigasi Profil (JIKA ADA ELEMEN UI-NYA) ---
        // Pastikan Anda memiliki ImageView dengan ID ivHeaderUserProfile di fragment_heart.xml
        // dan action action_heartFragment_to_profileFragment di nav_graph.xml
        // Contoh jika ImageView profil ada langsung di binding FragmentHeartBinding:
        if (binding.ivHeaderUserProfile != null) { // Ganti ivHeaderUserProfile dengan ID yang benar
            binding.ivHeaderUserProfile.setOnClickListener(v -> {
                try {
                    NavHostFragment.findNavController(HeartFragment.this)
                            .navigate(R.id.action_heartFragment_to_profileFragment);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Navigasi ke Profil belum siap.", Toast.LENGTH_SHORT).show();
                    Log.e("HeartFragment", "Navigasi ke profil gagal. Action ID 'action_heartFragment_to_profileFragment' mungkin hilang atau salah.", e);
                }
            });
        } else {
            // Jika ivHeaderUserProfile ada di dalam layout yang di-include (misal, custom_header.xml)
            // dan custom_header.xml memiliki ID, Anda mungkin perlu mengaksesnya melalui:
            // if (binding.namaIdIncludeLayout.ivHeaderUserProfile != null) { ... }
            // Atau jika tidak ada elemen UI profil di layout ini, bagian ini bisa diabaikan.
            Log.d("HeartFragment", "ImageView untuk navigasi profil (ivHeaderUserProfile) tidak ditemukan di layout.");
        }
        // --- Akhir Setup Navigasi Profil ---


        binding.btnGrooming.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(HeartFragment.this)
                        .navigate(R.id.action_heartFragment_to_groomingFragment);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Grooming belum siap.", Toast.LENGTH_SHORT).show();
                Log.e("HeartFragment", "Navigasi ke grooming gagal.", e);
            }
        });

        binding.btnDoctor.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(HeartFragment.this)
                        .navigate(R.id.action_heartFragment_to_doctorFragment);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Doctor belum siap.", Toast.LENGTH_SHORT).show();
                Log.e("HeartFragment", "Navigasi ke doctor gagal.", e);
            }
        });

        setupRecyclerViews();

        viewModel.groomingList.observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) { // Tambahkan pengecekan null
                groomingAdapter.updateData(appointments);
            }
        });

        viewModel.doctorList.observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) { // Tambahkan pengecekan null
                doctorAdapter.updateData(appointments);
            }
        });
    }

    private void setupRecyclerViews() {
        groomingAdapter = new AppointmentAdapter(requireContext(), new ArrayList<>(), this::navigateToDetail); // Menggunakan method reference
        binding.recyclerGroomingAppointments.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerGroomingAppointments.setAdapter(groomingAdapter);

        doctorAdapter = new AppointmentAdapter(requireContext(), new ArrayList<>(), this::navigateToDetail); // Menggunakan method reference
        binding.recyclerDoctorAppointments.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerDoctorAppointments.setAdapter(doctorAdapter);
    }

    private void navigateToDetail(AppointmentData appointment) {
        if (appointment == null || appointment.getPetName() == null) { // Pengecekan null untuk data appointment
            Toast.makeText(requireContext(), "Data appointment tidak lengkap.", Toast.LENGTH_SHORT).show();
            Log.w("HeartFragment", "Gagal navigasi ke detail: data appointment tidak lengkap.");
            return;
        }
        try {
            // Pastikan argumen di nav_graph.xml untuk action ini sesuai (misal: android:name="appointmentId" atau "petName")
            HeartFragmentDirections.ActionHeartFragmentToAppointmentDetailFragment action =
                    HeartFragmentDirections.actionHeartFragmentToAppointmentDetailFragment(appointment.getPetName()); // Mengirim nama hewan sebagai contoh

            // Jika Anda ingin mengirim ID unik:
            // HeartFragmentDirections.ActionHeartFragmentToAppointmentDetailFragment action =
            //        HeartFragmentDirections.actionHeartFragmentToAppointmentDetailFragment(appointment.getId()); // Pastikan AppointmentData punya getId()

            NavHostFragment.findNavController(HeartFragment.this).navigate(action);

        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Navigasi ke Detail Appointment belum siap.", Toast.LENGTH_SHORT).show();
            Log.e("HeartFragment", "Navigasi ke detail appointment gagal.", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}