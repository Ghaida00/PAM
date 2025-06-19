package com.example.projectakhir.ui.shop.Catalog; // PERUBAHAN: dari ui.viewmodel ke ui.viewmodel.shop

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.example.projectakhir.data.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CatalogViewModel extends ViewModel {
    private ProductRepository productRepository;
    private KeranjangRepository cartRepository;

    public LiveData<List<Product>> products;
    private MutableLiveData<Boolean> _addToCartStatus = new MutableLiveData<>();
    public LiveData<Boolean> addToCartStatus = _addToCartStatus;


    public CatalogViewModel() {
        productRepository = new ProductRepository();
        cartRepository = new KeranjangRepository();
        products = productRepository.getProducts();
    }

    public void addToCart(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null && product != null && product.getId() != null && !product.getId().isEmpty()) {
            try {
                KeranjangItem cartItem = new KeranjangItem(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), 1);
                cartRepository.addCartItem(userId, cartItem)
                    .addOnSuccessListener(aVoid -> _addToCartStatus.postValue(true))
                    .addOnFailureListener(e -> {
                        _addToCartStatus.postValue(false);
                        e.printStackTrace();
                    });
            } catch (Exception e) {
                e.printStackTrace();
                _addToCartStatus.postValue(false);
            }
        } else {
            _addToCartStatus.postValue(false);
        }
    }
}