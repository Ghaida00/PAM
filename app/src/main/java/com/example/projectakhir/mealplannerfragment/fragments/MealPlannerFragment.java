package com.example.projectakhir.mealplannerfragment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projectakhir.R;

public class MealPlannerFragment extends Fragment {

    private CardView cardFoodRecommendation;
    private CardView cardCustomMenu;
    private Button btnChoose;
    private TextView tvTitle;
    private TextView tvDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cardFoodRecommendation = view.findViewById(R.id.card_food_recommendation);
        cardCustomMenu = view.findViewById(R.id.card_custom_menu);
//        btnChoose = view.findViewById(R.id.btn_choose);
        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);

        tvTitle.setText(R.string.meal_planner_title);
        tvDescription.setText(R.string.meal_planner_description);

        cardFoodRecommendation.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_meal_to_foodRecommendationFragment)
        );
        cardCustomMenu.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_meal_to_yourCustomMenuFragment)
        );
//        btnChoose.setOnClickListener(v ->
//                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_meal_to_yourCustomMenuFragment)
//        );
    }
}