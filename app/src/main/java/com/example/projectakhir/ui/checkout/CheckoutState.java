package com.example.projectakhir.ui.checkout;

public abstract class CheckoutState {
    private CheckoutState() {}

    public static final class Loading extends CheckoutState {
        public static final Loading INSTANCE = new Loading();
        private Loading() {}
    }

    public static final class Success extends CheckoutState {
        private final String orderId;
        private final double totalAmount;

        public Success(String orderId, double totalAmount) {
            this.orderId = orderId;
            this.totalAmount = totalAmount;
        }

        public String getOrderId() {
            return orderId;
        }

        public double getTotalAmount() {
            return totalAmount;
        }
    }

    public static final class Error extends CheckoutState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public boolean isLoading() {
        return this instanceof Loading;
    }

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isError() {
        return this instanceof Error;
    }

    public Loading asLoading() {
        return (Loading) this;
    }

    public Success asSuccess() {
        return (Success) this;
    }

    public Error asError() {
        return (Error) this;
    }
} 