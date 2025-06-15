// package com.example.projectakhir.ui.auth.LoginViewModel.java

package com.example.projectakhir.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.ui.auth.AuthManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.AuthCredential;

public class LoginViewModel extends ViewModel {
    private AuthManager authManager;
    private MutableLiveData<Boolean> _loginResult = new MutableLiveData<>();
    public LiveData<Boolean> loginResult = _loginResult;

    public LoginViewModel() {
        authManager = new AuthManager();
    }

    public void loginUser(String email, String password) {
        authManager.loginUser(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        _loginResult.postValue(task.isSuccessful());
                    }
                });
    }

    /**
     * Menangani autentikasi Firebase dengan kredensial Google.
     * Hasil akan di-post ke loginResult LiveData.
     * @param credential Kredensial autentikasi Google dari GoogleSignInAccount.
     */
    public void signInWithGoogleCredential(AuthCredential credential) {
        authManager.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        _loginResult.postValue(task.isSuccessful());
                    }
                });
    }

    // Anda bisa menambahkan metode lain di sini sesuai kebutuhan,
    // seperti reset password, update profil, dll.
}
