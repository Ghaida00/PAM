package com.example.projectakhir.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectakhir.R;
import com.example.projectakhir.data.repository.UserReport;
import java.util.List;

public class ComplaintReportsAdapter extends RecyclerView.Adapter<ComplaintReportsAdapter.ViewHolder> {

    private final List<UserReport> reports;
    private final OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onApprove(UserReport report);
        void onReject(UserReport report);
    }

    public ComplaintReportsAdapter(List<UserReport> reports, OnItemInteractionListener listener) {
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserReport report = reports.get(position);
        holder.title.setText("Complaint: " + report.getJenisHewan());
        holder.detail.setText("Reporter: " + report.getNamaPelapor());
        holder.status.setText("Status: " + report.getStatus());

        boolean isProcessed = !"Diajukan".equalsIgnoreCase(report.getStatus());
        holder.approveButton.setEnabled(!isProcessed);
        holder.rejectButton.setEnabled(!isProcessed);

        holder.approveButton.setOnClickListener(v -> listener.onApprove(report));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(report));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, detail, status;
        Button approveButton, rejectButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_admin_title);
            detail = itemView.findViewById(R.id.item_admin_detail);
            status = itemView.findViewById(R.id.item_admin_status);
            approveButton = itemView.findViewById(R.id.btn_approve);
            rejectButton = itemView.findViewById(R.id.btn_reject);
        }
    }
}