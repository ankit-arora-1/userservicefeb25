package com.scaler.userservice.services;

import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;

public interface UserService {
    public Token login(String email, String password);
    public User signUp(String name, String email, String password);
    public User validateToken(String token);
    public void logout(String token);
}
