package com.scaler.userservice.dtos;

import com.scaler.userservice.models.Token;

public class LoginResponseDto {
    private Token token;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
