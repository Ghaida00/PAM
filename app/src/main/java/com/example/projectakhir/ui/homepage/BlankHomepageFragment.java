package com.example.projectakhir.ui.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectakhir.R;
import java.util.ArrayList;
import java.util.List;

public class BlankHomepageFragment extends Fragment {

    private RecyclerView rvExplore;
    private com.example.projectakhir.ui.homepage.ExploreAdapter exploreAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank_homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvExplore = view.findViewById(R.id.rv_explore);
        rvExplore.setLayoutManager(new GridLayoutManager(getContext(), 2));
        exploreAdapter = new com.example.projectakhir.ui.homepage.ExploreAdapter(getContext(), getExploreItems());
        rvExplore.setAdapter(exploreAdapter);

//        TextView tvProfileName = view.findViewById(R.id.tv_profile_name);
//        ImageButton btnMenu = view.findViewById(R.id.btn_menu);

//        tvProfileName.setText("DirtyCat");
//        btnMenu.setOnClickListener(v -> {
            // Open drawer or menu (if you have one)
//        });

        // REMOVE: bottom nav click listeners here!
    }

    private List<ExploreItem> getExploreItems() {
        List<ExploreItem> items = new ArrayList<>();
        items.add(new ExploreItem(
                "https://i.pinimg.com/736x/80/47/85/80478528c7f89c14b29f636a608176b8.jpg",
                "Royal Canin: Hair & Skin",
                "Rp 49.999",
                ""
        ));
        items.add(new ExploreItem(
                "https://i.pinimg.com/736x/03/e2/4b/03e24b8737a48ade5f4ce0e62adc082a.jpg",
                "Casper",
                "Surabaya",
                ""
        ));
        items.add(new ExploreItem(
                "https://i.pinimg.com/736x/dc/d1/8e/dcd18e57e091829de954be8a1cdc63d5.jpg",
                "DR. Bambang",
                "Jakarta",
                ""
        ));
        items.add(new ExploreItem(
                "https://i.pinimg.com/736x/20/83/08/208308dc6015c9ddb54fa7f42302158d.jpg",
                "Royal Canin: Hair & Skin",
                "Rp 49.999",
                ""
        ));
        return items;
    }
}