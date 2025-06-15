package com.example.projectakhir.mealplannerfragment.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectakhir.R;
import com.example.projectakhir.mealplannerfragment.adapters.CustomizedMenuAdapter;
import com.example.projectakhir.mealplannerfragment.models.CartItem;
import com.example.projectakhir.mealplannerfragment.models.CustomMenu;
import com.example.projectakhir.mealplannerfragment.models.FoodItem;
import com.example.projectakhir.mealplannerfragment.utils.CartManager;
import com.example.projectakhir.mealplannerfragment.utils.FirebaseManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class CustomizedMenuFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvTitle;
    private EditText etDate;
    private RecyclerView recyclerCustomizedMenu;
    private Button btnFinish;

    private CustomizedMenuAdapter adapter;
    private Date selectedDate = new Date();
    private Map<String, FoodItem> selectedMeals = new HashMap<>();
    private List<Map.Entry<String, FoodItem>> menuEntries = new ArrayList<>();
    private static final String[] MEAL_CATEGORIES = {
            "Sarapan", "Snack", "Makan siang", "Snack", "Makan malam"
    };
    private int nextCategoryIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customized_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnBack = view.findViewById(R.id.btn_back);
        tvTitle = view.findViewById(R.id.tv_title);
        etDate = view.findViewById(R.id.et_date);
        recyclerCustomizedMenu = view.findViewById(R.id.recycler_customized_menu);
        btnFinish = view.findViewById(R.id.btn_finish);

        tvTitle.setText("Customized menu");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        etDate.setText(sdf.format(selectedDate));

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        etDate.setOnClickListener(v -> showDatePicker());
        btnFinish.setOnClickListener(v -> {
            saveCustomMenu();
        });

        adapter = new CustomizedMenuAdapter(requireContext(), menuEntries);
        recyclerCustomizedMenu.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCustomizedMenu.setAdapter(adapter);

        adapter.setOnCustomizedMenuItemListener((category, position) -> {
            selectedMeals.remove(category);
            menuEntries.remove(position);
            adapter.notifyDataSetChanged();
        });

        loadSelectedCartItems();
    }

    private void loadSelectedCartItems() {
        List<CartItem> selectedCartItems = CartManager.getInstance().getSelectedCartItems();
        selectedMeals.clear();
        nextCategoryIndex = 0;
        for (CartItem cartItem : selectedCartItems) {
            for (int i = 0; i < cartItem.getQuantity(); i++) {
                if (nextCategoryIndex < MEAL_CATEGORIES.length) {
                    String category = MEAL_CATEGORIES[nextCategoryIndex];
                    selectedMeals.put(category, cartItem.getFoodItem());
                    nextCategoryIndex++;
                }
            }
        }
        updateMenuEntries();
    }

    private void updateMenuEntries() {
        menuEntries.clear();
        menuEntries.addAll(selectedMeals.entrySet());
        adapter.updateData(menuEntries);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year1, monthOfYear, dayOfMonth);
                    selectedDate = newDate.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                    etDate.setText(sdf.format(selectedDate));
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void saveCustomMenu() {
        if (selectedMeals.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one meal to your menu", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomMenu customMenu = new CustomMenu();
        customMenu.setDate(selectedDate);

        for (Map.Entry<String, FoodItem> entry : selectedMeals.entrySet()) {
            customMenu.addMeal(entry.getKey(), entry.getValue());
        }

        FirebaseManager.saveCustomMenu(customMenu, new FirebaseManager.OnCustomMenuSavedListener() {
            @Override
            public void onCustomMenuSaved(CustomMenu menu) {
                Toast.makeText(requireContext(), "Menu saved successfully!", Toast.LENGTH_SHORT).show();
                CartManager.getInstance().clearSelectedItems();
                // Back to YourCustomMenuFragment
                NavHostFragment.findNavController(CustomizedMenuFragment.this).navigate(R.id.action_customizedMenuFragment_to_navigation_meal);
            }
            @Override
            public void onCustomMenuSaveFailed(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to save menu: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}