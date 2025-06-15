package com.example.projectakhir.ui.homepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.ui.homepage.ExploreItem;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {
    private final List<ExploreItem> items;
    private final Context context;

    public ExploreAdapter(Context context, List<ExploreItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_explore, parent, false);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        ExploreItem item = items.get(position);
        Glide.with(context).load(item.getImageUrl()).into(holder.imgExplore);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.tvPrice.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ExploreViewHolder extends RecyclerView.ViewHolder {
        ImageView imgExplore;
        TextView tvTitle, tvSubtitle, tvPrice;
        ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            imgExplore = itemView.findViewById(R.id.img_explore);
            tvTitle = itemView.findViewById(R.id.tv_explore_title);
            tvSubtitle = itemView.findViewById(R.id.tv_explore_subtitle);
            tvPrice = itemView.findViewById(R.id.tv_explore_price);
        }
    }
}