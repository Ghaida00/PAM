package com.example.projectakhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
        // Set item pertama sebagai terpilih secara default
        if (!categoryList.isEmpty()) {
            categoryList.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);

        holder.btnCategory.setOnClickListener(v -> {
            if (listener != null) {
                int oldSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Update status selected pada model
                categoryList.get(oldSelectedPosition).setSelected(false);
                categoryList.get(selectedPosition).setSelected(true);

                // Beri tahu adapter perubahan data agar UI terupdate
                notifyItemChanged(oldSelectedPosition);
                notifyItemChanged(selectedPosition);

                listener.onCategoryClick(category, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Button btnCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCategory = itemView.findViewById(R.id.btnCategory);
        }

        void bind(Category category) {
            btnCategory.setText(category.getName());
//            btnCategory.setChecked(category.isSelected());
        }
    }
}