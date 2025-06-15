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
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.FirestoreDataSeeder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Inisialisasi ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Inisialisasi Views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavView = findViewById(R.id.bottom_navigation_view);

        // Setup NavController (sama seperti kode Anda)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            throw new IllegalStateException("NavHostFragment not found!");
        }

        // Tentukan halaman utama (Top-level destinations)
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.blankHomepageFragment);
        topLevelDestinations.add(R.id.adoptHomeFragment);
        topLevelDestinations.add(R.id.heartFragment);
        // topLevelDestinations.add(R.id.profileFragment);

        // Konfigurasi AppBar (PENTING untuk tombol kembali otomatis)
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        // Hubungkan NavController dengan ActionBar (Toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // Hubungkan NavController dengan BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavView, navController);

        // Panggil seeder untuk menambahkan data (HANYA UNTUK PERCOBAAN)
        // HAPUS ATAU BERI KOMENTAR BARIS INI SETELAH DATA BERHASIL DITAMBAHKAN
        // FirestoreDataSeeder.addSampleServices(FirebaseFirestore.getInstance());

        // Otak dari logika tampilan App Bar
        setupAppBarAndNavVisibility();
    }

    private void setupAppBarAndNavVisibility() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Tentukan halaman utama (Top-level destinations)
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.blankHomepageFragment);
            topLevelDestinations.add(R.id.adoptHomeFragment);
            topLevelDestinations.add(R.id.heartFragment);
            // topLevelDestinations.add(R.id.profileFragment);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;

            // Logika untuk menyembunyikan/menampilkan
            boolean isTopLevel = topLevelDestinations.contains(destination.getId());
            boolean isAuthOrProfileScreen = destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerFragment ||
                    destination.getId() == R.id.profileFragment;

            if (isAuthOrProfileScreen) {
                actionBar.hide();
                bottomNavView.setVisibility(View.GONE);
            } else {
                actionBar.show();
                bottomNavView.setVisibility(View.VISIBLE);

                if (isTopLevel) {
                    // Jika di halaman utama, tampilkan layout profil kustom
                    setupCustomActionBar();
                } else {
                    // Jika di halaman lain, kembalikan ke tampilan default (hanya judul)
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                }
            }
        });
    }

    private void setupCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        // Inflate layout kustom kita
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.action_bar_profile, null);

        // Set data ke view kustom
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

        // Tambahkan fungsi klik ke layout kustom
        customView.setOnClickListener(v -> {
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment);
            }
        });

        // Terapkan view kustom ke ActionBar
        actionBar.setCustomView(customView);
    }


    // Method ini PENTING untuk membuat tombol kembali di Toolbar berfungsi
    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null && NavigationUI.navigateUp(navController, appBarConfiguration))
                || super.onSupportNavigateUp();
    }
}