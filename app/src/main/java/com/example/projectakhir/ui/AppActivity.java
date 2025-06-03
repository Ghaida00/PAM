package com.example.projectakhir.ui; // Atau package Anda

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectakhir.R; // R file Anda
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AppActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan menggunakan layout activity_app.xml
        setContentView(R.layout.activity_app); // [cite: 49]

        // 1. Temukan Toolbar dan set sebagai ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar); // ID dari activity_app.xml
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // 2. Temukan NavHostFragment dan dapatkan NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_container); // ID dari activity_app.xml
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            // Handle error jika NavHostFragment tidak ditemukan
            throw new IllegalStateException("NavHostFragment not found!");
        }

        // 3. Temukan BottomNavigationView
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation_view); // ID dari activity_app.xml

        // 4. Definisikan Tujuan Level Atas (Top-Level Destinations)
        // ID di sini HARUS SAMA dengan ID fragment di nav_graph.xml DAN di bottom_nav_menu.xml
        Set<Integer> topLevelDestinations = new HashSet<>();
        // topLevelDestinations.add(R.id.homeFragment); // Jika ada fragment home
        topLevelDestinations.add(R.id.adoptHomeFragment); // ID dari nav_graph & menu [cite: 53, 50]
        topLevelDestinations.add(R.id.heartFragment);    // ID dari nav_graph & menu [cite: 53, 50]
        topLevelDestinations.add(R.id.profileFragment);  // <-- Add ProfileFragment ID here

        // Tambahkan ID fragment top-level lainnya jika ada (misal: shop, profile)

        // 5. Buat AppBarConfiguration
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        // 6. Setup ActionBar & BottomNavigationView dengan NavController
        // Ini akan menangani judul di Toolbar, tombol Up/Back, dan klik BottomNav
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController);

    }

    // 7. Handle tombol Up/Back di ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        // Pastikan navController tidak null sebelum digunakan
        return (navController != null && NavigationUI.navigateUp(navController, appBarConfiguration))
                || super.onSupportNavigateUp();
    }
}