package com.example.projectakhir.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.R;
import com.example.projectakhir.adapters.YourPetAdapter; // <-- Import adapter yang benar
import com.example.projectakhir.data.Hewan; // <-- Import model Hewan
import com.example.projectakhir.databinding.FragmentYourPetListBinding;
import java.util.ArrayList;

public class YourPetListFragment extends Fragment {

    private FragmentYourPetListBinding binding;
    private YourPetListViewModel viewModel;
    private YourPetAdapter adapter; // <-- Tipe data adapter diubah

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentYourPetListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(YourPetListViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong dan listener
        adapter = new YourPetAdapter(requireContext(), new ArrayList<>(), pet -> {
            // Aksi ketika item di-klik, misalnya navigasi ke detail
            // Contoh: Navigasi ke DetailHewanFragment yang sudah ada
            // Pastikan Anda sudah menambahkan action di nav_graph.xml dari YourPetListFragment ke DetailHewanFragment
            try {
                // YourPetListFragmentDirections.ActionYourPetListFragmentToDetailHewanFragment action =
                //         YourPetListFragmentDirections.actionYourPetListFragmentToDetailHewanFragment(pet.getId());
                // NavHostFragment.findNavController(this).navigate(action);
                Toast.makeText(getContext(), "Clicked on " + pet.getNama(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Navigation to pet detail is not set up.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerYourPets.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerYourPets.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBackYourPet.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.btnAddPet.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_yourPetListFragment_to_addPetFragment);
        });

        binding.btnPut.setOnClickListener(v -> {
            // Navigasi ke fragment "Put for Adoption"
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_yourPetListFragment_to_putForAdoptionFragment);
        });
    }

    private void observeViewModel() {
        // Perbarui data di adapter ketika LiveData berubah
        viewModel.petList.observe(getViewLifecycleOwner(), userPets -> {
            if (userPets != null) {
                adapter.updateData(userPets); // Panggil metode updateData
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarYourPet.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerYourPets.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Muat ulang data setiap kali fragment ini ditampilkan kembali
        // untuk memastikan data terbaru (misalnya setelah menambah pet baru)
        viewModel.loadPets();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}