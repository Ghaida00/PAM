package com.example.projectakhir.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projectakhir.R;
import com.example.projectakhir.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.txtUserName.setText(user.name);
                binding.txtUserEmail.setText(user.email);
                // For avatar, you'd use a library like Glide or Picasso, or set a local drawable
                // For now, assuming 'kucing_oren_profile' is in drawables
                if (user.avatarUrl != null && !user.avatarUrl.isEmpty()){
                    int imageResource = getResources().getIdentifier(user.avatarUrl, "drawable", requireContext().getPackageName());
                    if (imageResource != 0) {
                        binding.imgUserProfile.setImageResource(imageResource);
                    } else {
                        binding.imgUserProfile.setImageResource(R.drawable.agus); // Fallback
                    }
                } else {
                    binding.imgUserProfile.setImageResource(R.drawable.agus); // Fallback
                }
            }
        });

        setupNavigationListeners();

        viewModel.navigateTo.observe(getViewLifecycleOwner(), event -> {
            ProfileViewModel.NavigationTarget target = event.getContentIfNotHandled();
            if (target != null) {
                handleNavigation(target);
            }
        });
    }

    private void setupNavigationListeners() {
        binding.itemPersonalDetail.setOnClickListener(v -> viewModel.onPersonalDetailClicked());
        binding.itemYourPet.setOnClickListener(v -> viewModel.onYourPetClicked());
        binding.itemDeliveryAddress.setOnClickListener(v -> viewModel.onDeliveryAddressClicked());
        binding.itemPaymentMethod.setOnClickListener(v -> viewModel.onPaymentMethodClicked());
        binding.itemAbout.setOnClickListener(v -> viewModel.onAboutClicked());
        binding.itemHelp.setOnClickListener(v -> viewModel.onHelpClicked());
        binding.itemLogout.setOnClickListener(v -> viewModel.onLogoutClicked());
    }

    private void handleNavigation(ProfileViewModel.NavigationTarget target) {
        // Ensure you have defined these destinations and actions in your nav_graph.xml
        try {
            switch (target) {
                case PERSONAL_DETAIL:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_personalDetailFragment);
                    break;
                case YOUR_PET:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_yourPetListFragment);
                    break;
                case DELIVERY_ADDRESS:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_deliveryAddressFragment);
                    break;
                case PAYMENT_METHOD:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_paymentMethodFragment);
                    break;
                case ABOUT:
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_aboutAppFragment);
                    break;
                case HELP:
                    // Potentially navigate to a HelpFragment or show a dialog/WebView
                    Toast.makeText(getContext(), "Help clicked (Not implemented)", Toast.LENGTH_SHORT).show();
                    break;
                case LOGOUT:
                    showLogoutConfirmationDialog();
                    break;
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Navigation for " + target.name() + " not yet implemented or action ID is wrong.", Toast.LENGTH_LONG).show();
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // TODO: Implement actual logout logic (clear user session, navigate to login screen)
                    Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
                    // Example: NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_loginFragment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}