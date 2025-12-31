# TRMS Owner App - Module 1: Project Setup & Authentication (Java)

## Context
Creating an Android Owner mobile app for Taxi Revenue Management System (TRMS) that connects to an existing Node.js backend.

**Backend Repository:** C-gana/taxi-revenue-management-system---admin-web-app
**Language:** Java
**API Base URL:** To be configured (e.g., http://10.0.2.2:3000/ for emulator)

## Module 1 Objective
Set up Android project foundation and implement complete authentication flow with secure token storage.

---

## 1. Project Setup

### Create New Android Project
- **Name:** TRMS Owner
- **Package:** com.trms.owner
- **Language:** Java
- **Minimum SDK:** API 26 (Android 8.0)
- **Target SDK:** API 33 (Android 13)

### Dependencies (build.gradle - Module level)
```gradle
dependencies {
    // Core Android
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Lifecycle & ViewModel
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    
    // Gson
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Secure Storage
    implementation 'androidx. security:security-crypto:1.1.0-alpha06'
}
```

### Enable Java 8 (build.gradle - Module level)
```gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion. VERSION_1_8
    }
}
```

---

## 2. Extract Colors from Web App

**Task:** Go to repository CSS files and extract colors.

**Create `res/values/colors.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- TODO: Extract from web app CSS -->
    <color name="primary">#2196F3</color>
    <color name="primary_dark">#1976D2</color>
    <color name="accent">#FF9800</color>
    <color name="success">#4CAF50</color>
    <color name="warning">#FFC107</color>
    <color name="danger">#F44336</color>
    <color name="background">#F5F5F5</color>
    <color name="surface">#FFFFFF</color>
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
    <color name="divider">#E0E0E0</color>
</resources>
```

**Create `res/values/themes.xml`:**
```xml
<resources>
    <style name="Theme.TRMSOwner" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>
    </style>
</resources>
```

---

## 3. Data Models

**Create `com. trms.owner.data. model` package:**

### User. java
```java
package com.trms.owner.data.model;

import java.util.List;

public class User {
    private String user_id;
    private String full_name;
    private String role;
    private String phone_number;
    private String email;
    private List<String> vehicles;
    
    // Getters and Setters
    public String getUserId() { return user_id; }
    public void setUserId(String user_id) { this.user_id = user_id; }
    
    public String getFullName() { return full_name; }
    public void setFullName(String full_name) { this.full_name = full_name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getPhoneNumber() { return phone_number; }
    public void setPhoneNumber(String phone_number) { this.phone_number = phone_number; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<String> getVehicles() { return vehicles; }
    public void setVehicles(List<String> vehicles) { this.vehicles = vehicles; }
}
```

### LoginRequest.java
```java
package com.trms.owner.data.model;

public class LoginRequest {
    private String phone_number;
    private String password;
    
    public LoginRequest(String phone_number, String password) {
        this.phone_number = phone_number;
        this.password = password;
    }
    
    public String getPhoneNumber() { return phone_number; }
    public String getPassword() { return password; }
}
```

### LoginResponse.java
```java
package com.trms.owner.data.model;

public class LoginResponse {
    private boolean success;
    private String token;
    private User user;
    private String error;
    
    public boolean isSuccess() { return success; }
    public String getToken() { return token; }
    public User getUser() { return user; }
    public String getError() { return error; }
}
```

---

## 4. API Configuration

**Create `com.trms.owner.data.api` package:**

### ApiConfig.java
```java
package com.trms.owner.data.api;

public class ApiConfig {
    // Change for production
    public static final String BASE_URL = "http://10.0.2.2:3000/";
    public static final long TIMEOUT = 30; // seconds
}
```

### AuthApiService.java
```java
package com.trms.owner.data.api;

import com.trms.owner.data.model. LoginRequest;
import com.trms.owner.data.model. LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("api/mobile/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
```

### RetrofitClient.java
```java
package com.trms.owner.data.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance;
    private Retrofit retrofit;
    
    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor. setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(ApiConfig.TIMEOUT, TimeUnit. SECONDS)
                .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .build();
        
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig. BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    public AuthApiService getAuthApi() {
        return retrofit.create(AuthApiService.class);
    }
}
```

---

## 5. Secure Token Storage

**Create `com.trms.owner.data.local` package:**

### TokenManager.java
```java
package com.trms.owner.data. local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.google.gson.Gson;
import com.trms.owner.data.model. User;

public class TokenManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER = "user_data";
    
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    public TokenManager(Context context) {
        gson = new Gson();
        
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to regular SharedPreferences
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }
    
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        sharedPreferences. edit().putString(KEY_USER, userJson).apply();
    }
    
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }
    
    public void clearAuth() {
        sharedPreferences.edit().clear().apply();
    }
}
```

---

## 6. Repository Layer

**Create `com.trms.owner.data.repository` package:**

### AuthRepository.java
```java
package com.trms.owner.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.trms.owner.data.api.AuthApiService;
import com.trms.owner.data.api.RetrofitClient;
import com.trms.owner.data.local.TokenManager;
import com. trms.owner.data.model.LoginRequest;
import com. trms.owner.data.model.LoginResponse;
import com. trms.owner.data.model.User;
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
                        tokenManager.saveUser(loginResponse. getUser());
                        result. setValue(Result.success(loginResponse.getUser()));
                    } else {
                        String error = loginResponse.getError() != null ? 
                                loginResponse.getError() : "Login failed";
                        result.setValue(Result.error(error));
                    }
                } else {
                    result.setValue(Result.error("Server error:  " + response.code()));
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
        tokenManager. clearAuth();
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
        
        public T getData() { return data; }
        public String getError() { return error; }
        public boolean isSuccess() { return success; }
    }
}
```

---

## 7. Login ViewModel

**Create `com.trms.owner.ui.auth` package:**

### LoginViewModel.java
```java
package com.trms.owner.ui.auth;

import androidx.lifecycle.LiveData;
import androidx. lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.trms.owner.data. model.User;
import com.trms.owner.data.repository. AuthRepository;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        loginState.setValue(LoginState.idle());
    }
    
    public LiveData<LoginState> getLoginState() {
        return loginState;
    }
    
    public void login(String phoneNumber, String password) {
        // Validate
        if (! isValidPhoneNumber(phoneNumber)) {
            loginState.setValue(LoginState.error("Invalid phone format.  Use +265XXXXXXXXX"));
            return;
        }
        
        if (password.length() < 8) {
            loginState.setValue(LoginState.error("Password must be at least 8 characters"));
            return;
        }
        
        loginState.setValue(LoginState.loading());
        
        LiveData<AuthRepository.Result<User>> result = authRepository.login(phoneNumber, password);
        result.observeForever(userResult -> {
            if (userResult.isSuccess()) {
                loginState.setValue(LoginState.success(userResult.getData()));
            } else {
                loginState. setValue(LoginState.error(userResult.getError()));
            }
        });
    }
    
    private boolean isValidPhoneNumber(String phone) {
        Pattern pattern = Pattern.compile("^\\+265\\d{9}$");
        return pattern. matcher(phone).matches();
    }
    
    // State class
    public static class LoginState {
        public enum Status { IDLE, LOADING, SUCCESS, ERROR }
        
        private Status status;
        private User user;
        private String error;
        
        private LoginState(Status status, User user, String error) {
            this.status = status;
            this.user = user;
            this.error = error;
        }
        
        public static LoginState idle() {
            return new LoginState(Status.IDLE, null, null);
        }
        
        public static LoginState loading() {
            return new LoginState(Status. LOADING, null, null);
        }
        
        public static LoginState success(User user) {
            return new LoginState(Status. SUCCESS, user, null);
        }
        
        public static LoginState error(String error) {
            return new LoginState(Status. ERROR, null, error);
        }
        
        public Status getStatus() { return status; }
        public User getUser() { return user; }
        public String getError() { return error; }
    }
}
```

### LoginViewModelFactory.java
```java
package com.trms.owner.ui.auth;

import androidx.annotation.NonNull;
import androidx. lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.trms.owner. data.repository.AuthRepository;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    private AuthRepository authRepository;
    
    public LoginViewModelFactory(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
```

---

## 8. Login Screen UI

### res/layout/activity_login.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns: app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background"
    android:fillViewport="true">

    <androidx.constraintlayout.widget. ConstraintLayout
        android: layout_width="match_parent"
        android:layout_height="wrap_content"
        android: padding="24dp">

        <!-- Logo -->
        <ImageView
            android:id="@+id/ivLogo"
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:layout_marginTop="48dp"
            android:contentDescription="@string/app_logo"
            android:src="@mipmap/ic_launcher"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <!-- Title -->
        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:text="@string/owner_login"
            android:textColor="@color/text_primary"
            android:textSize="28sp"
            android:textStyle="bold"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/ivLogo" />

        <!-- Phone Input -->
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilPhone"
            style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="48dp"
            android:hint="@string/phone_number"
            app:layout_constraintTop_toBottomOf="@id/tvTitle"
            app:prefixText="+265">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etPhone"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="phone"
                android:maxLength="9" />
        </com.google.android. material.textfield.TextInputLayout>

        <!-- Password Input -->
        <com.google.android. material.textfield.TextInputLayout
            android:id="@+id/tilPassword"
            style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:hint="@string/password"
            app:endIconMode="password_toggle"
            app:layout_constraintTop_toBottomOf="@id/tilPhone">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etPassword"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="textPassword" />
        </com.google. android.material.textfield.TextInputLayout>

        <!-- Error Text -->
        <TextView
            android:id="@+id/tvError"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:textColor="@color/danger"
            android:textSize="14sp"
            android:visibility="gone"
            app:layout_constraintTop_toBottomOf="@id/tilPassword" />

        <!-- Login Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnLogin"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:layout_marginTop="32dp"
            android:text="@string/login"
            android:textSize="16sp"
            app:cornerRadius="8dp"
            app:layout_constraintTop_toBottomOf="@id/tvError" />

        <!-- Progress Bar -->
        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:visibility="gone"
            app:layout_constraintBottom_toBottomOf="@id/btnLogin"
            app:layout_constraintEnd_toEndOf="@id/btnLogin"
            app:layout_constraintStart_toStartOf="@id/btnLogin"
            app:layout_constraintTop_toTopOf="@id/btnLogin" />

    </androidx.constraintlayout. widget.ConstraintLayout>
</ScrollView>
```

### LoginActivity.java
```java
package com.trms.owner.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google. android.material.textfield.TextInputEditText;
import com. trms.owner.MainActivity;
import com.trms. owner.R;
import com. trms.owner.data.local.TokenManager;
import com. trms.owner.data.repository.AuthRepository;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        setContentView(R. layout.activity_login);
        
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
                    showError(state. getError());
                    break;
                case IDLE:
                    showLoading(false);
                    break;
            }
        });
    }
    
    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View. VISIBLE : View.GONE);
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
```

---

## 9. String Resources

### res/values/strings.xml
```xml
<resources>
    <string name="app_name">TRMS Owner</string>
    <string name="owner_login">Owner Login</string>
    <string name="phone_number">Phone Number</string>
    <string name="password">Password</string>
    <string name="login">Login</string>
    <string name="app_logo">App Logo</string>
</resources>
```

---

## 10. AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.trms.owner">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android: label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.TRMSOwner"
        android:usesCleartextTraffic="true">
        
        <activity
            android:name=". ui.auth.LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android. intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity
            android: name=".MainActivity"
            android:exported="false" />
    </application>

</manifest>
```

---

## 11. Placeholder MainActivity

**Create `com.trms.owner` package:**

### MainActivity.java
```java
package com.trms.owner;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.trms.owner.data.local.TokenManager;
import com.trms.owner.data.model.User;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Temporary layout for testing
        TextView textView = new TextView(this);
        textView.setText("Login Successful!\n\nModule 1 Complete");
        textView.setTextSize(20);
        textView.setPadding(50, 50, 50, 50);
        
        // Show logged in user
        TokenManager tokenManager = new TokenManager(this);
        User user = tokenManager.getUser();
        if (user != null) {
            textView.append("\n\nWelcome:  " + user.getFullName());
            textView.append("\nPhone: " + user.getPhoneNumber());
        }
        
        setContentView(textView);
    }
}
```

---

## Testing Module 1

### Test Cases:
1. ✅ Launch app → Login screen displays
2. ✅ Enter invalid phone (e.g., "12345") → Shows error
3. ✅ Enter short password (< 8 chars) → Shows error
4. ✅ Enter valid credentials → Shows loading, navigates on success
5. ✅ Enter wrong credentials → Shows API error message
6. ✅ Close app and reopen → Auto-login to MainActivity
7. ✅ No internet → Shows network error

### Test Login Credentials:
Use credentials from your backend database for testing.

**Example test:**
- Phone: +265991234567
- Password: password123

---

## Deliverables for Module 1

✅ Complete project structure
✅ Colors extracted from web app
✅ Data models (User, LoginRequest, LoginResponse)
✅ Retrofit API setup
✅ Secure token storage with EncryptedSharedPreferences
✅ AuthRepository with login/logout
✅ LoginViewModel with validation
✅ Professional login UI
✅ Auto-login functionality
✅ Error handling and loading states

**Next Module:** Vehicle Selection & Dashboard