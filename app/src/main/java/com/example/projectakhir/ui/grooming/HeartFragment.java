package com.example.projectakhir.ui.grooming;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Import Toast untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment; // Untuk Navigasi
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.AppointmentAdapter;
import com.example.projectakhir.data.AppointmentData; // Import data model
import com.example.projectakhir.databinding.FragmentHeartBinding; // Nama class binding

import java.util.ArrayList;

public class HeartFragment extends Fragment {

    private FragmentHeartBinding binding; // View Binding
    private AppointmentAdapter groomingAdapter;
    private AppointmentAdapter doctorAdapter;
    private HeartViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHeartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(HeartViewModel.class);

        // --- Kode dari onCreate HeartActivity dipindahkan ke sini ---

        // Setup Tombol Navigasi (Grooming & Doctor)
        binding.btnGrooming.setOnClickListener(v -> {
            try {
                // Navigasi ke GroomingFragment (pastikan action ada di nav_graph.xml)
                NavHostFragment.findNavController(HeartFragment.this)
                        .navigate(R.id.action_heartFragment_to_groomingFragment); // Ganti ID jika perlu
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Grooming belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDoctor.setOnClickListener(v -> {
            try {
                // Navigasi ke DoctorFragment (pastikan action ada di nav_graph.xml)
                NavHostFragment.findNavController(HeartFragment.this)
                        .navigate(R.id.action_heartFragment_to_doctorFragment); // Ganti ID jika perlu
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Doctor belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup RecyclerViews
        setupRecyclerViews();

        // Observe LiveData dari ViewModel
        viewModel.groomingList.observe(getViewLifecycleOwner(), appointments -> {
            groomingAdapter.updateData(appointments);
        });

        viewModel.doctorList.observe(getViewLifecycleOwner(), appointments -> {
            doctorAdapter.updateData(appointments);
        });

        // --- Akhir kode dari onCreate HeartActivity ---
    }

    // Fungsi setup RecyclerViews dipindahkan ke sini
    private void setupRecyclerViews() {
        // Penting: Modifikasi AppointmentAdapter untuk menerima listener klik
        // agar navigasi ditangani oleh Fragment, bukan Adapter.

        // Setup RecyclerView Grooming
        groomingAdapter = new AppointmentAdapter(requireContext(), new ArrayList<>(), appointment -> {
            // Handle klik item grooming -> Navigasi ke AppointmentDetailFragment
            navigateToDetail(appointment);
        });
        binding.recyclerGroomingAppointments.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerGroomingAppointments.setAdapter(groomingAdapter);

        // Setup RecyclerView Doctor
        doctorAdapter = new AppointmentAdapter(requireContext(), new ArrayList<>(), appointment -> {
            // Handle klik item doctor -> Navigasi ke AppointmentDetailFragment
            navigateToDetail(appointment);
        });
        binding.recyclerDoctorAppointments.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerDoctorAppointments.setAdapter(doctorAdapter);
    }

    // Fungsi helper untuk navigasi ke detail
    private void navigateToDetail(AppointmentData appointment) {
        try {
            // Buat action di nav_graph.xml dari heartFragment ke appointmentDetailFragment
            // Kirim data yang diperlukan sebagai argumen (contoh: ID atau data Parcelable/Serializable)
            HeartFragmentDirections.ActionHeartFragmentToAppointmentDetailFragment action =
                    HeartFragmentDirections.actionHeartFragmentToAppointmentDetailFragment(appointment.getPetName()); // Contoh kirim nama saja, sesuaikan argumen

            // Jika mengirim data lengkap, pastikan AppointmentData Parcelable/Serializable
            // action.setAppointmentData(appointment);

            NavHostFragment.findNavController(HeartFragment.this).navigate(action);

        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Navigasi ke Detail Appointment belum siap.", Toast.LENGTH_SHORT).show();
        }
        // TODO: Hapus intent lama di dalam AppointmentAdapter
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting: Bersihkan referensi binding
    }
}