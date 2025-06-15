package com.example.projectakhir.mealplannerfragment.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectakhir.R;
import com.example.projectakhir.mealplannerfragment.adapters.FoodItemAdapter;
import com.example.projectakhir.mealplannerfragment.models.FoodItem;
import com.example.projectakhir.mealplannerfragment.utils.CartManager;
import com.example.projectakhir.mealplannerfragment.utils.FirebaseManager;

import java.util.*;

public class FoodSelectionFragment extends Fragment {

    private ImageButton btnBack;
    private EditText etSearch;
    private RecyclerView recyclerFoodItems;
    private Button btnNext;
    private Button btnBack2;
    private ProgressBar progressBar;

    private FoodItemAdapter adapter;
    private List<FoodItem> allFoodItems = new ArrayList<>();
    private List<FoodItem> filteredFoodItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnBack = view.findViewById(R.id.btn_back);
        etSearch = view.findViewById(R.id.et_search);
        recyclerFoodItems = view.findViewById(R.id.recycler_food_items);
        btnNext = view.findViewById(R.id.btn_next);
        btnBack2 = view.findViewById(R.id.btn_back2);
        progressBar = view.findViewById(R.id.progress_bar);

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        btnBack2.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        btnNext.setOnClickListener(v -> {
            if (adapter.getSelectedItems().isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one food item", Toast.LENGTH_SHORT).show();
            } else {
                // Add selected items to cart with quantity 1, don't remove existing items
                for (FoodItem item : adapter.getSelectedItems()) {
                    CartManager.getInstance().updateItem(item, 1);
                }
                NavHostFragment.findNavController(this).navigate(R.id.action_foodSelectionFragment_to_cartFragment);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodItems(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        adapter = new FoodItemAdapter(requireContext(), filteredFoodItems);
        adapter.setSelectMode(true);
        recyclerFoodItems.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerFoodItems.setAdapter(adapter);

        // No need for setOnFoodItemClickListener: let checkbox handle selection

        loadFoodItems();
    }

    private void loadFoodItems() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseManager.getAllFoodItems(new FirebaseManager.OnFoodItemsLoadedListener() {
            @Override
            public void onFoodItemsLoaded(List<FoodItem> foodItems) {
                allFoodItems = foodItems;
                filteredFoodItems.clear();
                filteredFoodItems.addAll(allFoodItems);
                adapter.updateData(filteredFoodItems);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFoodItemsLoadFailed(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to load food items: " + errorMessage, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filterFoodItems(String query) {
        filteredFoodItems.clear();
        if (query.isEmpty()) {
            filteredFoodItems.addAll(allFoodItems);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (FoodItem item : allFoodItems) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredFoodItems.add(item);
                }
            }
        }
        adapter.updateData(filteredFoodItems);
    }
}