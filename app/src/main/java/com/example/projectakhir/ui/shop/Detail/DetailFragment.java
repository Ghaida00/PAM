package com.example.projectakhir.ui.shop.Detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DetailViewModel detailViewModel;
    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductRating, tvProductDescription, tvProductStock;
    private Button btnAddToCart;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        
        // Initialize views
        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductPrice = view.findViewById(R.id.tvProductPrice);
        tvProductRating = view.findViewById(R.id.tvProductRating);
        tvProductDescription = view.findViewById(R.id.tvProductDescription);
        tvProductStock = view.findViewById(R.id.tvProductStock);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get product ID from arguments
        String productId = null;
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }

        if (productId != null) {
            detailViewModel.fetchProductById(productId);
        } else {
            Toast.makeText(requireContext(), "Product ID not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        // Observe product data
        detailViewModel.getProductLiveData().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                // Update UI with product data
                tvProductName.setText(product.getName());
                tvProductPrice.setText(String.format("Rp%,.2f", product.getPrice()));
                tvProductDescription.setText(product.getDescription());
                tvProductStock.setText(String.format("Stok: %d", product.getStock()));
                
                // Load product image
                Glide.with(requireContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(ivProductImage);

                // Set up add to cart button
                btnAddToCart.setOnClickListener(v -> {
                    detailViewModel.addToCart(product);
                });
            } else {
                Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });

        // Observe add to cart status
        detailViewModel.getAddToCartStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Produk berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Gagal menambahkan produk ke keranjang", Toast.LENGTH_SHORT).show();
            }
        });
    }
}