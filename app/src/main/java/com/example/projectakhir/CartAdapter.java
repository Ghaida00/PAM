package com.example.projectakhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDeleteClick(CartItem item, int position);
        void onQuantityIncrease(CartItem item, int position);
        void onQuantityDecrease(CartItem item, int position);
    }

    public CartAdapter(List<CartItem> cartItemList, OnItemActionListener listener) {
        this.cartItemList = cartItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.productName.setText(item.getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedPrice = formatter.format(item.getPrice()).replace("Rp", "Rp ");
        holder.productPrice.setText(formattedPrice);

        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(20));

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.productImage);

        // Set Listeners
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item, holder.getAdapterPosition());
            }
        });

        holder.plusButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityIncrease(item, holder.getAdapterPosition());
            }
        });

        holder.minusButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityDecrease(item, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void setCartItems(List<CartItem> newItems) {
        this.cartItemList.clear();
        this.cartItemList.addAll(newItems);
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        ImageView deleteButton;
        TextView productQuantity;
        ImageView minusButton;
        ImageView plusButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            minusButton = itemView.findViewById(R.id.minusButton);
            plusButton = itemView.findViewById(R.id.plusButton);
        }
    }
}