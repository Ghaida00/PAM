package com.example.projectakhir.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// A simple User data class (can be more complex)
class User {
    String name;
    String email;
    String avatarUrl; // Or resource ID

    User(String name, String email, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }
}

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;

    private final MutableLiveData<Event<NavigationTarget>> _navigateTo = new MutableLiveData<>();
    public LiveData<Event<NavigationTarget>> navigateTo = _navigateTo;


    public ProfileViewModel() {
        // Load user data - replace with actual data fetching
        _user.setValue(new User("Ayam Oyen", "ayamoyen@gmail.com", "kucing_oren_profile")); // [cite: 6, 11]
    }

    public void onPersonalDetailClicked() {
        _navigateTo.setValue(new Event<>(NavigationTarget.PERSONAL_DETAIL));
    }

    public void onYourPetClicked() {
        _navigateTo.setValue(new Event<>(NavigationTarget.YOUR_PET));
    }

    public void onDeliveryAddressClicked() {
        _navigateTo.setValue(new Event<>(NavigationTarget.DELIVERY_ADDRESS));
    }

    public void onPaymentMethodClicked() {
        _navigateTo.setValue(new Event<>(NavigationTarget.PAYMENT_METHOD));
    }

    public void onAboutClicked() {
        _navigateTo.setValue(new Event<>(NavigationTarget.ABOUT));
    }

    public void onHelpClicked() {
        _navigateTo.setValue(new Event<>(NavigationTarget.HELP));
    }
    public void onLogoutClicked() {
        // Handle logout logic (e.g., clear session, navigate to login)
        _navigateTo.setValue(new Event<>(NavigationTarget.LOGOUT));
    }

    enum NavigationTarget {
        PERSONAL_DETAIL,
        YOUR_PET,
        DELIVERY_ADDRESS,
        PAYMENT_METHOD,
        ABOUT,
        HELP,
        LOGOUT
    }

    // Helper class for single-event LiveData
    public static class Event<T> {
        private T content;
        private boolean hasBeenHandled = false;

        public Event(T content) {
            this.content = content;
        }

        public T getContentIfNotHandled() {
            if (hasBeenHandled) {
                return null;
            } else {
                hasBeenHandled = true;
                return content;
            }
        }

        public T peekContent() {
            return content;
        }
    }
}