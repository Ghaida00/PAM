package com.example.projectakhir.ui.shop.Catalog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.CartItem;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.data.repository.CartRepository;
import com.example.projectakhir.data.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CatalogViewModel extends ViewModel {
    private ProductRepository productRepository;
    private CartRepository cartRepository;

    public LiveData<List<Product>> products;
    private MutableLiveData<Boolean> _addToCartStatus = new MutableLiveData<>();
    public LiveData<Boolean> addToCartStatus = _addToCartStatus;


    public CatalogViewModel() {
        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        products = productRepository.getProducts();
    }

    public void addToCart(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            CartItem cartItem = new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), 1);
            cartRepository.addCartItem(userId, cartItem)
                    .addOnCompleteListener(task -> _addToCartStatus.postValue(task.isSuccessful()));
        } else {
            _addToCartStatus.postValue(false);
        }
    }
}