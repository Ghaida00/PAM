// package com.example.projectakhir.ui.AppActivity.java

package com.example.projectakhir.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation; // Penting: import Navigation
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.FirestoreDataSeeder; // Tetap ada jika Anda menggunakannya
import com.example.projectakhir.ui.UserViewModel; // FIXED: Mengoreksi jalur import UserViewModel
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

        // Setup NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            throw new IllegalStateException("NavHostFragment not found!");
        }

        // Tentukan halaman utama (Top-level destinations) - ini yang akan memengaruhi tombol kembali di toolbar
        // Fragmen-fragmen ini akan muncul di bottom navigation dan tidak memiliki tombol kembali di toolbar
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.blankHomepageFragment); // Home
        topLevelDestinations.add(R.id.adoptHomeFragment);     // Adopt
        topLevelDestinations.add(R.id.catalogFragment);       // Catalog
        topLevelDestinations.add(R.id.keranjangFragment);     // Cart
        topLevelDestinations.add(R.id.profileFragment);       // Profile

        // Konfigurasi AppBar (PENTING untuk tombol kembali otomatis)
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        // Hubungkan NavController dengan ActionBar (Toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // Hubungkan NavController dengan BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavView, navController);

        // Logic untuk menentukan ke mana harus pergi dari SplashActivity (atau jika Activity dibuat ulang)
        if (savedInstanceState == null) {
            String destinationFragment = getIntent().getStringExtra("destination_fragment");
            if ("login".equals(destinationFragment)) {
                // Navigasi ke LoginFragment dan kosongkan back stack hingga start destination
                navController.navigate(R.id.loginFragment, null, new androidx.navigation.NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
            } else if ("homepage".equals(destinationFragment)) {
                // Navigasi ke blankHomepageFragment dan kosongkan back stack hingga start destination
                navController.navigate(R.id.blankHomepageFragment, null, new androidx.navigation.NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
            } else {
                // Default ke LoginFragment jika tidak ada intent extra atau tidak dikenali
                navController.navigate(R.id.loginFragment, null, new androidx.navigation.NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
            }
        }

        // Panggil seeder untuk menambahkan data (HANYA UNTUK PERCOBAAN)
        // HAPUS ATAU BERI KOMENTAR BARIS INI SETELAH DATA BERHASIL DITAMBAHKAN
        // FirestoreDataSeeder.addSampleServices(FirebaseFirestore.getInstance());

        // Otak dari logika tampilan App Bar
        setupAppBarAndNavVisibility();
    }

    private void setupAppBarAndNavVisibility() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Tentukan halaman utama (Top-level destinations)
            // Ini harus sama persis dengan yang di onCreate
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.blankHomepageFragment);
            topLevelDestinations.add(R.id.adoptHomeFragment);
            topLevelDestinations.add(R.id.catalogFragment);
            topLevelDestinations.add(R.id.keranjangFragment);
            topLevelDestinations.add(R.id.profileFragment);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;

            // Logika untuk menyembunyikan/menampilkan
            boolean isTopLevel = topLevelDestinations.contains(destination.getId());
            boolean isAuthScreen = destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerFragment;

            // Daftar fragmen di mana *keduanya* (Toolbar & Bottom Nav) harus disembunyikan
            // Ini umumnya untuk layar penuh seperti checkout, pembayaran, detail item individual,
            // atau layar pengaturan yang tidak diakses dari bottom nav.
            boolean hideBoth = destination.getId() == R.id.reviewFragment ||
                    destination.getId() == R.id.notificationFragment ||
                    destination.getId() == R.id.checkoutFragment ||
                    destination.getId() == R.id.paymentFragment ||
                    destination.getId() == R.id.personalDetailFragment ||
                    destination.getId() == R.id.yourPetListFragment ||
                    destination.getId() == R.id.putForAdoptionFragment ||
                    destination.getId() == R.id.addPetFragment ||
                    destination.getId() == R.id.deliveryAddressFragment ||
                    destination.getId() == R.id.paymentMethodFragment ||
                    destination.getId() == R.id.aboutAppFragment ||
                    destination.getId() == R.id.daftarHewanFragment ||
                    destination.getId() == R.id.detailHewanFragment ||
                    destination.getId() == R.id.formAdopsiFragment ||
                    destination.getId() == R.id.formPengaduanFragment ||
                    destination.getId() == R.id.progresMainFragment ||
                    destination.getId() == R.id.progresAdopsiFragment ||
                    destination.getId() == R.id.progresPengaduanFragment ||
                    destination.getId() == R.id.detailSalonFragment ||
                    destination.getId() == R.id.detailVetFragment ||
                    destination.getId() == R.id.bookingFragment ||
                    destination.getId() == R.id.appointmentDetailFragment ||
                    destination.getId() == R.id.editProfileFragment; // Menambahkan ini

            if (isAuthScreen || hideBoth) {
                actionBar.hide();
                bottomNavView.setVisibility(View.GONE);
            } else {
                actionBar.show();
                bottomNavView.setVisibility(View.VISIBLE); // Pastikan bottom nav terlihat

                if (isTopLevel) {
                    // Jika di halaman utama (bottom nav items), tampilkan layout profil kustom
                    setupCustomActionBar();
                    userViewModel.loadUserProfile(); // Memuat data profil saat masuk ke top-level screen
                } else {
                    // Jika di halaman detail/sub-level, kembalikan ke tampilan default (hanya judul)
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
            // Navigasi ke profileFragment jika belum di sana
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
        // NavigationUI.navigateUp akan menangani navigasi ke atas
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
