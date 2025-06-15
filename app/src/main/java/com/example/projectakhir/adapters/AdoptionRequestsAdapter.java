package com.example.projectakhir.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectakhir.R;
import com.example.projectakhir.data.AdoptionRequest;
import java.util.List;

public class AdoptionRequestsAdapter extends RecyclerView.Adapter<AdoptionRequestsAdapter.ViewHolder> {

    private final List<AdoptionRequest> requests;
    private final OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onApprove(AdoptionRequest request);
        void onReject(AdoptionRequest request);
    }

    public AdoptionRequestsAdapter(List<AdoptionRequest> requests, OnItemInteractionListener listener) {
        this.requests = requests;
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
        AdoptionRequest request = requests.get(position);
        holder.title.setText("Adoption: " + request.getNamaHewan());
        holder.detail.setText("Applicant: " + request.getNamaHewan()); // Assuming getNamaHewan is applicant name
        holder.status.setText("Status: " + request.getStatus());

        boolean isProcessed = !"Diajukan".equalsIgnoreCase(request.getStatus());
        holder.approveButton.setEnabled(!isProcessed);
        holder.rejectButton.setEnabled(!isProcessed);

        holder.approveButton.setOnClickListener(v -> listener.onApprove(request));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
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