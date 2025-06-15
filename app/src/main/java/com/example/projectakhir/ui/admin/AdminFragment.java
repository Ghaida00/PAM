package com.example.projectakhir.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectakhir.adapters.AdoptionRequestsAdapter;
import com.example.projectakhir.adapters.ComplaintReportsAdapter;
import com.example.projectakhir.data.AdoptionRequest;
import com.example.projectakhir.data.repository.UserReport;
import com.example.projectakhir.databinding.FragmentAdminBinding;

public class AdminFragment extends Fragment {

    private FragmentAdminBinding binding;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();
        observeViewModel();
    }

    private void setupRecyclerViews() {
        binding.recyclerAdoptionRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerComplaintReports.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.adminProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });

        viewModel.updateStatus.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });

        viewModel.adoptionRequests.observe(getViewLifecycleOwner(), requests -> {
            AdoptionRequestsAdapter adapter = new AdoptionRequestsAdapter(requests, new AdoptionRequestsAdapter.OnItemInteractionListener() {
                @Override
                public void onApprove(AdoptionRequest request) {
                    viewModel.updateAdoptionRequestStatus(request.getId(), "Diterima");
                }

                @Override
                public void onReject(AdoptionRequest request) {
                    viewModel.updateAdoptionRequestStatus(request.getId(), "Ditolak");
                }
            });
            binding.recyclerAdoptionRequests.setAdapter(adapter);
        });

        viewModel.userReports.observe(getViewLifecycleOwner(), reports -> {
            ComplaintReportsAdapter adapter = new ComplaintReportsAdapter(reports, new ComplaintReportsAdapter.OnItemInteractionListener() {
                @Override
                public void onApprove(UserReport report) {
                    viewModel.updateUserReportStatus(report.getId(), "Selesai");
                }

                @Override
                public void onReject(UserReport report) {
                    viewModel.updateUserReportStatus(report.getId(), "Ditolak");
                }
            });
            binding.recyclerComplaintReports.setAdapter(adapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}