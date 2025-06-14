package com.example.projectakhir.newpawpal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectakhir.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Keranjang extends AppCompatActivity implements CartAdapter.OnItemActionListener {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

    private TextView tvOrderTotal;
    private TextView tvDeliveryFee;
    private TextView tvDiscount;
    private TextView tvGrandTotal;
    private EditText etVoucherCode;
    private Button btnUseVoucher;
    private Button btnCheckout;
    private ImageView backButton;

    // Dummy data untuk biaya dan diskon
    private double fixedDeliveryFee = 12000;
    private double voucherDiscount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        // Inisialisasi UI
        rvCartItems = findViewById(R.id.rvCartItems);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        etVoucherCode = findViewById(R.id.etVoucherCode);
        btnUseVoucher = findViewById(R.id.btnUseVoucher);
        btnCheckout = findViewById(R.id.btnCheckout);
        backButton = findViewById(R.id.backButton);

        // Setup RecyclerView
        cartItemList = new ArrayList<>();
        // Contoh data dummy untuk keranjang
        cartItemList.add(new CartItem("prod001", "Kalung Kucing", "https://i.imgur.com/gK4Y3kP.png", 14000, 3));
        cartItemList.add(new CartItem("prod002", "Makanan Kucing", "https://i.imgur.com/x0Z0Z0Z.png", 67000, 3));
        cartItemList.add(new CartItem("prod003", "Sisir Kucing", "https://i.imgur.com/y1Z1Z1Z.png", 10000, 1));

        cartAdapter = new CartAdapter(cartItemList, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);

        calculateTotals();

        // Listeners
        backButton.setOnClickListener(v -> onBackPressed());

        btnUseVoucher.setOnClickListener(v -> {
            applyVoucher();
        });

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(Keranjang.this, "Proceeding to Checkout!", Toast.LENGTH_SHORT).show();
            // Implementasi logika checkout Anda di sini
        });

        etVoucherCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Bisa reset voucher jika kode berubah
            }
        });
    }

    private void calculateTotals() {
        double orderTotal = 0;
        for (CartItem item : cartItemList) {
            orderTotal += item.getTotalItemPrice();
        }

        double finalDiscount = voucherDiscount;
        double grandTotal = orderTotal + fixedDeliveryFee - finalDiscount;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedOrderTotal = formatter.format(orderTotal).replace("Rp", "Rp ");
        String formattedDeliveryFee = formatter.format(fixedDeliveryFee).replace("Rp", "Rp ");
        String formattedDiscount = "-" + formatter.format(finalDiscount).replace("Rp", "Rp ");
        String formattedGrandTotal = formatter.format(grandTotal).replace("Rp", "Rp ");

        tvOrderTotal.setText(formattedOrderTotal);
        tvDeliveryFee.setText(formattedDeliveryFee);
        tvDiscount.setText(formattedDiscount);
        tvGrandTotal.setText(formattedGrandTotal);
    }

    private void applyVoucher() {
        String voucherCode = etVoucherCode.getText().toString().trim();
        if (voucherCode.equalsIgnoreCase("DISKON10")) {
            voucherDiscount = 12000;
            Toast.makeText(this, "Voucher DISKON10 diterapkan!", Toast.LENGTH_SHORT).show();
        } else if (voucherCode.isEmpty()) {
            voucherDiscount = 0;
            Toast.makeText(this, "Masukkan kode voucher.", Toast.LENGTH_SHORT).show();
        }
        else {
            voucherDiscount = 0;
            Toast.makeText(this, "Kode voucher tidak valid.", Toast.LENGTH_SHORT).show();
        }
        calculateTotals();
    }

    @Override
    public void onDeleteClick(CartItem item, int position) {
        cartItemList.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartItemList.size());
        calculateTotals();
        Toast.makeText(this, item.getName() + " dihapus dari keranjang.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuantityIncrease(CartItem item, int position) {
        item.setQuantity(item.getQuantity() + 1);
        cartAdapter.notifyItemChanged(position);
        calculateTotals();
    }

    @Override
    public void onQuantityDecrease(CartItem item, int position) {
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartAdapter.notifyItemChanged(position);
            calculateTotals();
        } else {
            onDeleteClick(item, position); // Hapus jika kuantitas jadi 0
        }
    }
}