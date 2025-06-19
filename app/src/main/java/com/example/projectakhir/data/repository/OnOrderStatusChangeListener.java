package com.example.projectakhir.data.repository;

import com.example.projectakhir.data.model.Order;

public interface OnOrderStatusChangeListener {
    void onStatusChanged(Order updatedOrder);
} 