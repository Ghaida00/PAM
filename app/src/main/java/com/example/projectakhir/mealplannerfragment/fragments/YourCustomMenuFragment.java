package com.example.projectakhir.mealplannerfragment.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectakhir.R;
import com.example.projectakhir.mealplannerfragment.adapters.CustomMenuAdapter;
import com.example.projectakhir.mealplannerfragment.models.CustomMenu;
import com.example.projectakhir.mealplannerfragment.models.FoodItem;
import com.example.projectakhir.mealplannerfragment.utils.FirebaseManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class YourCustomMenuFragment extends Fragment {

    private ImageButton btnBack, btnCart, btnCustom, btnAddMenu, btnCalendar, btnEdit;
    private TextView tvDayOfWeek;
    private RecyclerView recyclerCustomMenu;
    private Button btnAdd, btnRemove;
    private CustomMenuAdapter adapter;
    private CustomMenu customMenu;
    private Date selectedDate = new Date();
    private boolean isEditMode = false;
    private List<String> selectedCategories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_custom_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnBack = view.findViewById(R.id.btn_back);
        btnCart = view.findViewById(R.id.btn_cart);
        btnCustom = view.findViewById(R.id.btn_custom);
        btnAddMenu = view.findViewById(R.id.btn_add_menu);
        tvDayOfWeek = view.findViewById(R.id.tv_day_of_week);
        btnCalendar = view.findViewById(R.id.btn_calendar);
        btnEdit = view.findViewById(R.id.btn_edit);
        recyclerCustomMenu = view.findViewById(R.id.recycler_custom_menu);
        btnAdd = view.findViewById(R.id.btn_add);
        btnRemove = view.findViewById(R.id.btn_remove);

        updateDayOfWeek();
        btnAdd.setVisibility(View.GONE);
        btnRemove.setVisibility(View.GONE);

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        btnCalendar.setOnClickListener(v -> showDatePicker());
        btnEdit.setOnClickListener(v -> toggleEditMode());

        btnCart.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_yourCustomMenuFragment_to_cartFragment);
        });
        btnCustom.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_yourCustomMenuFragment_to_foodSelectionFragment);
        });
        btnAddMenu.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_yourCustomMenuFragment_to_makeYourOwnMenuFragment);
        });
        btnAdd.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_yourCustomMenuFragment_to_foodSelectionFragment);
        });
        btnRemove.setOnClickListener(v -> removeSelectedItems());

        adapter = new CustomMenuAdapter(requireContext(), new CustomMenu());
        recyclerCustomMenu.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCustomMenu.setAdapter(adapter);
        adapter.setOnCustomMenuItemListener((category, foodItem, position) -> {
            if (isEditMode) {
                if (!selectedCategories.contains(category)) {
                    selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }
            }
        });

        loadCustomMenu();
    }

    private void loadCustomMenu() {
        FirebaseManager.getCustomMenuForDate(selectedDate, new FirebaseManager.OnCustomMenuLoadedListener() {
            @Override
            public void onCustomMenuLoaded(CustomMenu menu) {
                customMenu = menu;
                adapter.updateData(customMenu);
                selectedCategories.clear();
            }
            @Override
            public void onCustomMenuLoadFailed(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to load custom menu: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
                    updateDayOfWeek();
                    loadCustomMenu();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateDayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
        String dayOfWeek = sdf.format(selectedDate);
        tvDayOfWeek.setText(dayOfWeek);
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        adapter.setEditMode(isEditMode);
        btnAdd.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        btnRemove.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        if (!isEditMode) {
            selectedCategories.clear();
        }
    }

    private void removeSelectedItems() {
        if (selectedCategories.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }
        for (String category : selectedCategories) {
            customMenu.removeMeal(category);
        }
        FirebaseManager.saveCustomMenu(customMenu, new FirebaseManager.OnCustomMenuSavedListener() {
            @Override
            public void onCustomMenuSaved(CustomMenu menu) {
                adapter.updateData(customMenu);
                selectedCategories.clear();
                Toast.makeText(requireContext(), "Menu Successfully Removed", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCustomMenuSaveFailed(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to update menu: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}