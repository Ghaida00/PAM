package com.example.projectakhir.mealplannerfragment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectakhir.R;
import com.example.projectakhir.mealplannerfragment.adapters.RecommendationAdapter;
import com.example.projectakhir.mealplannerfragment.models.FoodItem;
import com.example.projectakhir.mealplannerfragment.utils.FirebaseManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class FoodRecommendationFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvDayOfWeek;
    private RecyclerView recyclerRecommendations;
    private ProgressBar progressBar;

    private RecommendationAdapter adapter;
    private List<FoodItem> recommendedFoodItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_recommendation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnBack = view.findViewById(R.id.btn_back);
        tvDayOfWeek = view.findViewById(R.id.tv_day_of_week);
        recyclerRecommendations = view.findViewById(R.id.recycler_recommendations);
        progressBar = view.findViewById(R.id.progress_bar);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
        String dayOfWeek = sdf.format(calendar.getTime());
        tvDayOfWeek.setText(dayOfWeek);

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        adapter = new RecommendationAdapter(requireContext());
        recyclerRecommendations.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRecommendations.setAdapter(adapter);

        loadRecommendedFoodItems();
    }

    private void loadRecommendedFoodItems() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseManager.getRecommendedFoodItems(new FirebaseManager.OnFoodItemsLoadedListener() {
            @Override
            public void onFoodItemsLoaded(List<FoodItem> foodItems) {
                recommendedFoodItems = foodItems;
                processRecommendedItems();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFoodItemsLoadFailed(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to load recommendations: " + errorMessage, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void processRecommendedItems() {
        Map<String, List<FoodItem>> itemsByCategory = new HashMap<>();
        List<String> categories = Arrays.asList("Sarapan", "Snack", "Makan siang", "Snack", "Makan malam");
        for (String category : categories) {
            itemsByCategory.put(category, new ArrayList<>());
        }
        for (FoodItem item : recommendedFoodItems) {
            String category = item.getCategory();
            if (itemsByCategory.containsKey(category)) {
                itemsByCategory.get(category).add(item);
            }
        }
        adapter.setCategories(categories);
        adapter.setItemsByCategory(itemsByCategory);
    }
}