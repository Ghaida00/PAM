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
                // Untuk avatar, kamu bisa menggunakan library seperti Glide atau Picasso
                // Untuk saat ini, diasumsikan user.avatarUrl adalah nama drawable resource
                if (user.avatarUrl != null && !user.avatarUrl.isEmpty()){
                    // Pastikan avatarUrl adalah nama file drawable yang valid tanpa ekstensi
                    int imageResource = getResources().getIdentifier(user.avatarUrl, "drawable", requireContext().getPackageName());
                    if (imageResource != 0) {
                        binding.imgUserProfile.setImageResource(imageResource);
                    } else {
                        // Fallback jika resource tidak ditemukan
                        binding.imgUserProfile.setImageResource(R.drawable.agus); // Ganti 'agus' dengan drawable fallback defaultmu
                    }
                } else {
                    // Fallback jika avatarUrl kosong atau null
                    binding.imgUserProfile.setImageResource(R.drawable.agus); // Ganti 'agus' dengan drawable fallback defaultmu
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
        // Pastikan ID CardView di XML kamu adalah itemPersonalDetailCard, itemYourPetCard, dst.
        // Jika berbeda, sesuaikan nama variabel binding di bawah ini.
        binding.itemPersonalDetailCard.setOnClickListener(v -> viewModel.onPersonalDetailClicked());
        binding.itemYourPetCard.setOnClickListener(v -> viewModel.onYourPetClicked());
        binding.itemDeliveryAddressCard.setOnClickListener(v -> viewModel.onDeliveryAddressClicked());
        binding.itemPaymentMethodCard.setOnClickListener(v -> viewModel.onPaymentMethodClicked());
        binding.itemAboutCard.setOnClickListener(v -> viewModel.onAboutClicked());
        binding.itemHelpCard.setOnClickListener(v -> viewModel.onHelpClicked());
        binding.itemLogoutCard.setOnClickListener(v -> viewModel.onLogoutClicked());

        // Listener untuk tombol kembali di header (jika diperlukan)
        // binding.btnBack.setOnClickListener(v -> {
        //     // Aksi untuk tombol kembali, misalnya NavHostFragment.findNavController(this).popBackStack();
        // });
    }

    private void handleNavigation(ProfileViewModel.NavigationTarget target) {
        // Pastikan destinasi dan action sudah didefinisikan di nav_graph.xml
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
                    // Bisa navigasi ke HelpFragment atau tampilkan dialog/WebView
                    Toast.makeText(getContext(), "Help clicked (Fitur belum diimplementasi)", Toast.LENGTH_SHORT).show();
                    break;
                case LOGOUT:
                    showLogoutConfirmationDialog();
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Tangani jika action ID salah atau destinasi belum ada
            String message = "Navigasi untuk " + target.name() + " belum diimplementasi atau ID action salah.";
            if (getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
            // Log error untuk debugging
            // Log.e("ProfileFragment", message, e);
        }
    }

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return; // Hindari crash jika fragment detached

        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // TODO: Implementasikan logika logout sebenarnya
                    // (bersihkan sesi pengguna, navigasi ke layar login, dll.)
                    Toast.makeText(getContext(), "Berhasil logout!", Toast.LENGTH_SHORT).show();
                    // Contoh navigasi ke LoginFragment (pastikan action ID ada di nav_graph):
                    // NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_loginFragment);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting untuk menghindari memory leak dengan ViewBinding pada Fragment
    }
}