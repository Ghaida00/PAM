package com.example.projectakhir.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.KeranjangItem;
import java.util.ArrayList;
import java.util.List;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.CartViewHolder> {

    private List<KeranjangItem> cartItems;
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChange(KeranjangItem cartItem, int newQuantity);
        void onRemoveItem(KeranjangItem cartItem);
    }

    public KeranjangAdapter(List<KeranjangItem> cartItems, OnCartItemActionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateCartItems(List<KeranjangItem> newItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keranjang, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        KeranjangItem cartItem = cartItems.get(position);
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
        ImageView increaseQuantityButton;
        ImageView decreaseQuantityButton;
        ImageView removeCartItemButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cart_item_image);
            cartItemName = itemView.findViewById(R.id.cart_item_name);
            cartItemPrice = itemView.findViewById(R.id.cart_item_price);
            cartItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            increaseQuantityButton = itemView.findViewById(R.id.plusButton);
            decreaseQuantityButton = itemView.findViewById(R.id.minusButton);
            removeCartItemButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(KeranjangItem cartItem) {
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
