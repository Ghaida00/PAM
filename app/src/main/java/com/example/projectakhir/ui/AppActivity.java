package com.example.projectakhir.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.ui.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashSet;
import java.util.Set;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private UserViewModel userViewModel;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavView = findViewById(R.id.bottom_navigation_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            throw new IllegalStateException("NavHostFragment not found!");
        }

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.blankHomepageFragment);
        topLevelDestinations.add(R.id.adoptHomeFragment);
        topLevelDestinations.add(R.id.catalogFragment);
        topLevelDestinations.add(R.id.heartFragment);
        topLevelDestinations.add(R.id.profileFragment);

        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        String destinationFragment = getIntent().getStringExtra("destination_fragment");
        if (savedInstanceState == null) {
            if ("login".equals(destinationFragment)) {
                navController.navigate(R.id.loginFragment, null, new androidx.navigation.NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
            } else if ("homepage".equals(destinationFragment)) {
                navController.navigate(R.id.blankHomepageFragment, null, new androidx.navigation.NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
            } else {
                navController.navigate(R.id.loginFragment, null, new androidx.navigation.NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
            }
        }

        setupAppBarAndNavVisibility();
    }

    private void setupAppBarAndNavVisibility() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.blankHomepageFragment);
            topLevelDestinations.add(R.id.adoptHomeFragment);
            topLevelDestinations.add(R.id.catalogFragment);
            topLevelDestinations.add(R.id.heartFragment);
            topLevelDestinations.add(R.id.profileFragment);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;

            boolean isTopLevel = topLevelDestinations.contains(destination.getId());
            boolean isAuthScreen = destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerFragment;

            if (isAuthScreen) {
                actionBar.hide();
                bottomNavView.setVisibility(View.GONE);
            } else {
                actionBar.show();
                bottomNavView.setVisibility(View.VISIBLE);

                if (isTopLevel) {
                    setupCustomActionBar();
                } else {
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                }

                if (destination.getId() == R.id.reviewFragment ||
                        destination.getId() == R.id.cartFragment ||
                        destination.getId() == R.id.notificationFragment) {
                    bottomNavView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.action_bar_profile, null);

        CircleImageView profileImage = customView.findViewById(R.id.action_bar_profile_image);
        TextView profileName = customView.findViewById(R.id.action_bar_profile_name);

        userViewModel.userName.observe(this, name -> {
            if (name != null) profileName.setText(name);
        });

        userViewModel.profileImageUrl.observe(this, imageUrl -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_profile).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_profile);
            }
        });

        customView.setOnClickListener(v -> {
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment);
            }
        });

        actionBar.setCustomView(customView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null && NavigationUI.navigateUp(navController, appBarConfiguration))
                || super.onSupportNavigateUp();
    }
}
