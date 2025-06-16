package com.example.projectakhir.ui.shop.Detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.CartItem;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.data.repository.CartRepository;
import com.example.projectakhir.data.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;

public class DetailViewModel extends ViewModel {
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private MutableLiveData<Product> productLiveData;
    private MutableLiveData<Boolean> addToCartStatus;

    public DetailViewModel() {
        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        productLiveData = new MutableLiveData<>();
        addToCartStatus = new MutableLiveData<>();
    }

    public void fetchProductById(String productId) {
        productRepository.getProductById(productId).observeForever(product -> {
            productLiveData.postValue(product);
        });
    }

    public LiveData<Product> getProductLiveData() {
        return productLiveData;
    }

    public LiveData<Boolean> getAddToCartStatus() {
        return addToCartStatus;
    }

    public void addToCart(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
            FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
            
        if (userId != null) {
            CartItem cartItem = new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                1
            );
            
            cartRepository.addCartItem(userId, cartItem)
                .addOnCompleteListener(task -> {
                    addToCartStatus.postValue(task.isSuccessful());
                });
        } else {
            addToCartStatus.postValue(false);
        }
    }
}
