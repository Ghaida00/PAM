package com.example.projectakhir.mealplannerfragment.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projectakhir.R;
import com.example.projectakhir.mealplannerfragment.models.FoodItem;
import com.example.projectakhir.mealplannerfragment.utils.FirebaseManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Locale;
import java.util.UUID;

public class MakeYourOwnMenuFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvTitle;
    private EditText etName;
    private EditText etCalories;
    private Spinner spinnerCategory;
    private EditText etDescription;
    private ImageView imgFood;
    private View layoutUpload;
    private Button btnConfirm;
    private ProgressBar progressBar;

    private Uri imageUri;
    private String[] categories = {"Sarapan", "Snack", "Makan Siang", "Makan Malam"};
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_make_your_own_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnBack = view.findViewById(R.id.btn_back);
        tvTitle = view.findViewById(R.id.tv_title);
        etName = view.findViewById(R.id.et_food_name);
        etCalories = view.findViewById(R.id.et_calories);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        etDescription = view.findViewById(R.id.et_food_description);
        imgFood = view.findViewById(R.id.img_food);
        layoutUpload = view.findViewById(R.id.layout_upload);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        progressBar = view.findViewById(R.id.progress_bar);

        tvTitle.setText("Make Your Own Menu");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgFood.setImageURI(imageUri);
                        imgFood.setVisibility(View.VISIBLE);
                    }
                }
        );

        layoutUpload.setOnClickListener(v -> openImagePicker());

        btnConfirm.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadFoodWithImage();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return false;
        }
        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            return false;
        }
        if (caloriesStr.isEmpty()) {
            etCalories.setError("Calories is required");
            return false;
        }
        try {
            Integer.parseInt(caloriesStr);
        } catch (NumberFormatException e) {
            etCalories.setError("Invalid calorie value");
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadFoodWithImage() {
        progressBar.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageRef.child("food_images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> createFoodItem(uri.toString())))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirm.setEnabled(true);
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createFoodItem(String imageUrl) {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int calories = Integer.parseInt(etCalories.getText().toString().trim());
        String category = spinnerCategory.getSelectedItem().toString();

        FoodItem foodItem = new FoodItem(null, name, description, imageUrl, calories, category);

        FirebaseManager.addCustomFoodItem(foodItem, new FirebaseManager.OnFoodItemSavedListener() {
            @Override
            public void onFoodItemSaved(FoodItem item) {
                progressBar.setVisibility(View.GONE);
                showSuccessDialog();
            }
            @Override
            public void onFoodItemSaveFailed(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                Toast.makeText(requireContext(), "Failed to create menu item: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        Dialog successDialog = new Dialog(requireContext());
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.dialog_success);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCancelable(false);

        TextView tvMessage = successDialog.findViewById(R.id.tv_success_message);
        tvMessage.setText("Your Menu Successfully Added");

        successDialog.show();

        new android.os.Handler().postDelayed(() -> {
            successDialog.dismiss();
            NavHostFragment.findNavController(this).navigate(R.id.action_makeYourOwnMenuFragment_to_yourCustomMenuFragment);
        }, 2000);
    }
}