package com.cgana.trmsownerapp.data.model;

public class LoginRequest {
    private String phone_number;
    private String password;

    public LoginRequest(String phone_number, String password) {
        this.phone_number = phone_number;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public String getPassword() {
        return password;
    }
}

