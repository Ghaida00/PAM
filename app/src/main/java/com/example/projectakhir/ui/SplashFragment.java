package com.example.projectakhir.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.projectakhir.R;

public class SplashFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add click listeners for login and register buttons
        view.findViewById(R.id.buttonLogin).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_splashFragment_to_loginFragment));

        view.findViewById(R.id.buttonRegister).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_splashFragment_to_registerFragment));

        // Auto-navigate to login after 3 seconds
        new Handler().postDelayed(() -> {
            if (getView() != null) {
                Navigation.findNavController(getView()).navigate(R.id.action_splashFragment_to_loginFragment);
            }
        }, 3000);
    }
} 