package com.cgana.trmsownerapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cgana.trmsownerapp.data.local.TokenManager;
import com.cgana.trmsownerapp.data.model.User;

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
            textView.append("\n\nWelcome: " + user.getFullName());
            textView.append("\nPhone: " + user.getPhoneNumber());
        }

        setContentView(textView);
    }
}

