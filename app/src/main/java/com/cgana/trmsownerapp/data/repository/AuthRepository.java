package com.cgana.trmsownerapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cgana.trmsownerapp.data.api.AuthApiService;
import com.cgana.trmsownerapp.data.api.RetrofitClient;
import com.cgana.trmsownerapp.data.local.TokenManager;
import com.cgana.trmsownerapp.data.model.LoginRequest;
import com.cgana.trmsownerapp.data.model.LoginResponse;
import com.cgana.trmsownerapp.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private AuthApiService apiService;
    private TokenManager tokenManager;

    public AuthRepository(TokenManager tokenManager) {
        this.apiService = RetrofitClient.getInstance().getAuthApi();
        this.tokenManager = tokenManager;
    }

    public LiveData<Result<User>> login(String phoneNumber, String password) {
        MutableLiveData<Result<User>> result = new MutableLiveData<>();

        LoginRequest request = new LoginRequest(phoneNumber, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess() && loginResponse.getToken() != null
                            && loginResponse.getUser() != null) {
                        tokenManager.saveToken(loginResponse.getToken());
                        tokenManager.saveUser(loginResponse.getUser());
                        result.setValue(Result.success(loginResponse.getUser()));
                    } else {
                        String error = loginResponse.getError() != null ?
                                loginResponse.getError() : "Login failed";
                        result.setValue(Result.error(error));
                    }
                } else {
                    result.setValue(Result.error("Server error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                result.setValue(Result.error(t.getMessage()));
            }
        });

        return result;
    }

    public void logout() {
        tokenManager.clearAuth();
    }

    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }

    // Result wrapper
    public static class Result<T> {
        private T data;
        private String error;
        private boolean success;

        private Result(T data, String error, boolean success) {
            this.data = data;
            this.error = error;
            this.success = success;
        }

        public static <T> Result<T> success(T data) {
            return new Result<>(data, null, true);
        }

        public static <T> Result<T> error(String error) {
            return new Result<>(null, error, false);
        }

        public T getData() {
            return data;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}

