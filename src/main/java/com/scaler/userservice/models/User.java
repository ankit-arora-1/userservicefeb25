package com.scaler.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity(name = "users")
public class User extends BaseModel {
    private String name;
    private String email;
    private String hashedPassword;

    @ManyToMany
    private List<Role> roles;
}
