// package com.example.projectakhir.adapters;

package com.example.projectakhir.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Masih dipertahankan, meskipun di XML sekarang ImageView
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChange(CartItem cartItem, int newQuantity);
        void onRemoveItem(CartItem cartItem);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemActionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateCartItems(List<CartItem> newItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemName;
        TextView cartItemPrice;
        TextView cartItemQuantity;
        // Menggunakan ImageView karena di XML mereka adalah ImageView, bukan Button
        ImageView increaseQuantityButton; // Mengubah tipe dari Button ke ImageView
        ImageView decreaseQuantityButton; // Mengubah tipe dari Button ke ImageView
        ImageView removeCartItemButton;   // Mengubah tipe dari Button ke ImageView

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.productImage);
            cartItemName = itemView.findViewById(R.id.productName);
            cartItemPrice = itemView.findViewById(R.id.productPrice);
            cartItemQuantity = itemView.findViewById(R.id.productQuantity);
            // PERBAIKAN ID DAN TIPE DI SINI:
            increaseQuantityButton = itemView.findViewById(R.id.plusButton);  // Menggunakan ID dari XML
            decreaseQuantityButton = itemView.findViewById(R.id.minusButton); // Menggunakan ID dari XML
            removeCartItemButton = itemView.findViewById(R.id.deleteButton); // Menggunakan ID dari XML
        }

        public void bind(CartItem cartItem) {
            cartItemName.setText(cartItem.getProductName());
            cartItemPrice.setText(String.format("Rp %,.0f", cartItem.getProductPrice()));
            cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
            Glide.with(itemView.getContext())
                    .load(cartItem.getProductImageUrl())
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(cartItemImage);

            increaseQuantityButton.setOnClickListener(v -> listener.onQuantityChange(cartItem, cartItem.getQuantity() + 1));
            decreaseQuantityButton.setOnClickListener(v -> {
                int newQuantity = cartItem.getQuantity() - 1;
                if (newQuantity > 0) {
                    listener.onQuantityChange(cartItem, newQuantity);
                } else {
                    listener.onRemoveItem(cartItem);
                }
            });
            removeCartItemButton.setOnClickListener(v -> listener.onRemoveItem(cartItem));
        }
    }
}
