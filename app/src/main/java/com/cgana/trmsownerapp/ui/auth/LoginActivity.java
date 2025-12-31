package com.cgana.trmsownerapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cgana.trmsownerapp.MainActivity;
import com.cgana.trmsownerapp.R;
import com.cgana.trmsownerapp.data.local.TokenManager;
import com.cgana.trmsownerapp.data.repository.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;
    private LoginViewModel viewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        // Initialize ViewModel
        tokenManager = new TokenManager(this);
        AuthRepository authRepository = new AuthRepository(tokenManager);
        LoginViewModelFactory factory = new LoginViewModelFactory(authRepository);
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String phone = "+265" + etPhone.getText().toString().trim();
            String password = etPassword.getText().toString();
            viewModel.login(phone, password);
        });
    }

    private void observeViewModel() {
        viewModel.getLoginState().observe(this, state -> {
            switch (state.getStatus()) {
                case LOADING:
                    showLoading(true);
                    hideError();
                    break;
                case SUCCESS:
                    showLoading(false);
                    navigateToMain();
                    break;
                case ERROR:
                    showLoading(false);
                    showError(state.getError());
                    break;
                case IDLE:
                    showLoading(false);
                    break;
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        etPhone.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

